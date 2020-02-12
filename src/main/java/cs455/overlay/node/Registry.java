package cs455.overlay.node;

import cs455.overlay.transport.TCPSender;
import cs455.overlay.util.*;
import cs455.overlay.wireformats.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cs455.overlay.transport.TCPServerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Registry extends Node implements Protocol {

    private static Logger LOG = LogManager.getLogger(Registry.class);

    private RegistrationTable registrationTable;

    private int overlayNodeReportsTaskFinishedCounter;
    private final Object taskFinishedLock = new Object();

    private Map<Integer, TrafficSummary> trafficSummaryTable;

    private int overlayNodeReportsTrafficSummaryCounter;
    private final Object trafficSummaryLock = new Object();

    private Registry() {
        this.eventFactory = new EventFactory();
        this.registrationTable = new RegistrationTable();
    }

    public static void main(String[] args) {
        try {
            Registry registry = new Registry();
            ServerSocket serverSocket = new ServerSocket(5000);

            TCPServerThread server = new TCPServerThread(serverSocket, registry);
            LOG.info("Starting server thread...");
            (new Thread(server)).start();

            Scanner sc = new Scanner(System.in);
            while(sc.hasNextLine()) {
                String command = sc.nextLine();
                StringTokenizer st = new StringTokenizer(command);
                ArrayList<String> tokenList = new ArrayList<>();
                while(st.hasMoreTokens()) {
                    tokenList.add(st.nextToken());
                }
                if (tokenList.size() > 0) {
                    if (tokenList.get(0).equals("setup-overlay")) {
                        if (tokenList.size() == 2) {
                            try {
                                int routingTableSize = Integer.parseInt(tokenList.get(1));
                                registry.setupOverlay(routingTableSize);
                            } catch(NumberFormatException e) {
                                System.out.println("Unable to parse the number-of-routing-table-entries, it must be an integer value");
                            }
                        }
                        else {
                            System.out.println("Incorrect number of arguments provided for setup-overlay command");
                        }
                    }
                    if (tokenList.get(0).equals("start")) {
                        if (tokenList.size() == 2) {
                            try {
                                int numberOfMessages = Integer.parseInt(tokenList.get(1));
                                registry.initiateMessagingTask(numberOfMessages);
                            } catch (NumberFormatException e) {
                                System.out.println("Unable to parse the number-of-messages, it must be an integer value");
                            }
                        }
                        else {
                            System.out.println("Incorrect number of arguments provided for start command");
                        }
                    }
                    else {
                        System.out.println("Unknown argument provided");
                    }
                }
            }
        } catch (IOException ioe) {
            LOG.error(ioe.getMessage());
        }
    }

    @Override
    public void onEvent(Socket socket, Event event) {
        if (event != null) {
            try {
                if (event.getType() == OVERLAY_NODE_SENDS_REGISTRATION) {
                    LOG.info("Registering new messaging node...");
                    LOG.debug("bytes received from the OVERLAY_NODE_SENDS_REGISTRATION message: " + Arrays.toString(event.getBytes()));
                    handleOverlayNodeSendsRegistration(socket, event);
                }
                else if (event.getType() == OVERLAY_NODE_SENDS_DEREGISTRATION) {
                    LOG.info("Deregistering messaging node...");
                    LOG.debug("bytes received from the OVERLAY_NODE_SENDS_DEREGISTRATION message: " + Arrays.toString(event.getBytes()));
                    handleOverlayNodeSendsDeregistration(socket, event);
                }
                else if (event.getType() == NODE_REPORTS_OVERLAY_SETUP_STATUS) {
                    LOG.info("Received an overlay setup status from one of the messaging nodes...");
                    LOG.debug("bytes received from NODE_REPORTS_OVERLAY_SETUP_STATUS message: " + Arrays.toString(event.getBytes()));
                }
                else if (event.getType() == OVERLAY_NODE_REPORTS_TASK_FINISHED) {
                    LOG.info("An overlay node has reported that their task is finished...");
                    LOG.debug("bytes received from OVERLAY_NODE_REPORTS_TASK_FINISHED message: " + Arrays.toString(event.getBytes()));
                    incrementTaskFinishedCounter();
                }
                else if (event.getType() == OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY) {
                    LOG.info("An overlay node has reported their traffic summary...");
                    LOG.debug("bytes received from OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY message: " + Arrays.toString(event.getBytes()));
                    handleOverlayNodeReportsTrafficSummary(event);
                }
                else {
                    LOG.warn("Received an unknown event type: " + event.getType());
                }
            } catch (NullPointerException e) {
                LOG.error("A NullPointerException occurred while trying to get the event type", e);
            }
        }
        else {
            LOG.warn("The event received is null and will not be handled");
        }
    }

    private void handleOverlayNodeSendsRegistration(Socket socket, Event event) {
        try {
            OverlayNodeSendsRegistrationHandler handler = new OverlayNodeSendsRegistrationHandler(event);

            /* TODO NOTE:
             * I should probably add custom exceptions (mismatched or already registered)
             * to handle errors when adding new entries! returning boolean doesn't give
             * enough information to handle properly.
             */
            LOG.info("handleOverlayNodeSendsRegistration: Attempting to add new MessagingNode to the registration table...");
            MessagingNodeInfo messagingNodeInfo = constructNewMessagingNodeInfo(handler.getIPAddress(), handler.getPortNumber(), socket);
            int successStatus = -1;
            String informationString = "";
            if (Arrays.equals(handler.getIPAddress(), socket.getInetAddress().getAddress())) {
                if (registrationTable.addNewEntry(messagingNodeInfo)) {
                    successStatus = registrationTable.getID(messagingNodeInfo);
                    int numberOfEntries = registrationTable.countEntries();
                    informationString = "Registration  request  successful. The number of messaging nodes currently constituting the overlay is (" +
                    numberOfEntries + ")";
                } else {
                    // TODO this needs to be more specific, i.e. make new exceptions to handle specific cases
                    LOG.warn("handleOverlayNodeSendsRegistration: Failed to add MessagingNode to the table");
                    informationString = "Unable to add the MessagingNode to the registration table";
                }
            } else {
                LOG.warn("handleOverlayNodeSendsRegistration: Unable to register the MessagingNode because the IP addresses " +
                        "don't match. address in message: " + Arrays.toString(handler.getIPAddress()) + " address of socket: " +
                        Arrays.toString(socket.getInetAddress().getAddress()));
                informationString = "The IP address in the message does not match the actual IP address of the sender";
            }

            //Construct new RegistrationReportsRegistrationStatus message
            RegistryReportsRegistrationStatus status = new RegistryReportsRegistrationStatus(successStatus, informationString.getBytes());

            sendRegistryReportsRegistrationStatusMessage(socket, status);

        } catch (IOException ioe) {
            LOG.error("IOException occurred while trying to handle an OVERLAY_NODE_SENDS_REGISTRATION message", ioe);
        }
    }

    private MessagingNodeInfo constructNewMessagingNodeInfo(byte[] IPAddress, int portNumber, Socket socket) {
        LogicalNetworkAddress logicalAddress = new LogicalNetworkAddress(IPAddress, portNumber);
        return new MessagingNodeInfo(logicalAddress, socket);
    }

    private void sendRegistryReportsRegistrationStatusMessage(Socket socket, RegistryReportsRegistrationStatus status) {
        try {
            //Set up a socket to send back a REGISTRY_REPORTS_REGISTRATION_STATUS message
            TCPSender sender = new TCPSender(socket);
            sender.sendData(status.getBytes());
        } catch (IOException e) {
            LOG.error("An exception occurred while trying to send a REGISTRY_REPORTS_REGISTRATION_STATUS message", e);
        }
    }

    private void handleOverlayNodeSendsDeregistration(Socket socket, Event event) {
        try {
            OverlayNodeSendsDeregistrationHandler handler = new OverlayNodeSendsDeregistrationHandler(event);
            LogicalNetworkAddress networkAddress = new LogicalNetworkAddress(handler.getIPAddress(), handler.getPortNumber());
            MessagingNodeInfo recvdNodeInfo = new MessagingNodeInfo(networkAddress, socket);

            int successStatus = -1;
            String informationString = "";
            if (this.registrationTable.containsEntry(recvdNodeInfo)) {
                MessagingNodeInfo storedNodeInfo = this.registrationTable.getEntry(handler.getAssignedNodeID());
                if (storedNodeInfo.getNetworkAddress().equals(recvdNodeInfo.getNetworkAddress())) {
                    //deregister the node and return a success status
                    this.registrationTable.removeExistingEntry(handler.getAssignedNodeID(), recvdNodeInfo);
                    successStatus = handler.getAssignedNodeID();
                    informationString = "Deregistration request successful. The number of messaging nodes currently constituting the overlay is (" +
                            this.registrationTable.countEntries() + ")";
                } else {
                    //return error message because the network addresses don't match
                    informationString = "Deregistration request unsuccessful. The messaging node information provided does not match";
                }
            } else {
                //return an error message because there is no entry for the address in the message
                informationString = "Deregistration request unsuccessful. The messaging node is not currently registered";
            }

            RegistryReportsDeregistrationStatus deregistrationStatus = new
                    RegistryReportsDeregistrationStatus(successStatus, informationString.getBytes());

            TCPSender sender = new TCPSender(socket);

            sender.sendData(deregistrationStatus.getBytes());


        } catch(IOException e) {
            LOG.error("Unable to handle an OVERLAY_NODE_SENDS_DEREGISTRATION message", e);
        }

    }

    public void setupOverlay(int routingTableSize) {
        int[] nodeIDList = registrationTable.getNodeIDList();
        Arrays.sort(nodeIDList);
        LOG.debug("The sorted nodeIDList = " + Arrays.toString(nodeIDList));
        for(int i=0; i < nodeIDList.length; i++) {
            LOG.debug("Setting up routing table for node " + nodeIDList[i]);
            RoutingTable routingTable = new RoutingTable();
            for (int j=0; j < routingTableSize; j++) {
                int hopsAway = (int) Math.pow(2, j);
                int nextHopID = nodeIDList[(i + hopsAway) % nodeIDList.length];
                LOG.debug("Node ID of of node " + hopsAway + " hop away = " + nextHopID);
                MessagingNodeInfo info = registrationTable.getEntry(nextHopID);
                LogicalNetworkAddress networkAddress = info.getNetworkAddress();
                byte [] IPAddress = networkAddress.getIPAddress();
                LOG.debug("IP address of node " + hopsAway + " hop away = " + Arrays.toString(IPAddress));
                int portNumber = networkAddress.getPortNumber();
                LOG.debug("port Number of node " + hopsAway + " hop away = " + portNumber);
                RoutingEntry entry = new RoutingEntry(IPAddress, portNumber, nextHopID, hopsAway);
                routingTable.addRoutingEntry(entry);
            }
            RegistrySendsNodeManifest nodeManifest = new RegistrySendsNodeManifest(routingTableSize, routingTable, nodeIDList);
            Socket messagingNodeSocket = registrationTable.getEntry(nodeIDList[i]).getSocket();
            try {
                TCPSender sender = new TCPSender(messagingNodeSocket);
                sender.sendData(nodeManifest.getBytes());
            } catch (IOException e) {
                LOG.error("Unable to send REGISTRY_SENDS_NODE_MANIFEST to messaging node " + nodeIDList[i], e);
            }
        }
    }

    public void initiateMessagingTask(int numberOfMessages) {
        this.overlayNodeReportsTaskFinishedCounter = 0;
        RegistryRequestsTaskInitiate taskInitiate = new RegistryRequestsTaskInitiate(numberOfMessages);

        for(int nodeID : this.registrationTable.getNodeIDList()) {
            MessagingNodeInfo info = this.registrationTable.getEntry(nodeID);
            Socket socket = info.getSocket();
            try {
                TCPSender sender = new TCPSender(socket);
                sender.sendData(taskInitiate.getBytes());
            } catch (IOException e) {
                LOG.error("Unable to send REGISTRY_REQUEST_TASK_INITIATE message to node " + nodeID);
            }
        }

        LOG.info("Waiting for messaging nodes to report task finished...");
        waitForNodesToReportTaskFinished();
        LOG.info("All messaging nodes have reported task finished! sending requests for traffic summary in 10 seconds...");
        try {
            Thread.sleep(10000);
        } catch(InterruptedException e) {
            LOG.error("the thread was interrupted while waiting before printing traffic summaries", e);
        }
        requestTrafficSummary();
    }

    private void waitForNodesToReportTaskFinished() {
        // (Consumer thread) Wait for the nodes to send their OVERLAY_NODE_REPORTS_TASK_FINISHED messages
        int numberOfOverlayNodes = this.registrationTable.countEntries();

        try {
            synchronized (taskFinishedLock) {
                while(overlayNodeReportsTaskFinishedCounter < numberOfOverlayNodes) {
                    taskFinishedLock.wait();
                }
            }
        } catch(InterruptedException e) {
            LOG.error("An error occurred while waiting for the overlay nodes to finish reporting their tasks", e);
        }
    }

    private void incrementTaskFinishedCounter() {
        // (Producer thread) increment the counter when a node reports task finished and notify the consumer thread
        synchronized (taskFinishedLock) {
            this.overlayNodeReportsTaskFinishedCounter++;
            taskFinishedLock.notify();
        }
    }

    private void requestTrafficSummary() {
        this.trafficSummaryTable = new HashMap<>();
        this.overlayNodeReportsTrafficSummaryCounter = 0;
        RegistryRequestsTrafficSummary trafficSummaryRequest = new RegistryRequestsTrafficSummary();

        for(int nodeID : this.registrationTable.getNodeIDList()) {
            MessagingNodeInfo info = this.registrationTable.getEntry(nodeID);
            Socket socket = info.getSocket();
            try {
                TCPSender sender = new TCPSender(socket);
                sender.sendData(trafficSummaryRequest.getBytes());
            } catch (IOException e) {
                LOG.error("Unable to send REGISTRY_REQUESTS_TRAFFIC_SUMMARY to node " + nodeID);
            }
        }

        LOG.info("Waiting for messaging nodes to report traffic summaries");
        waitForNodesToReportTrafficSummaries();
        LOG.info("All messaging nodes have reported their traffic summaries!");
        LOG.info("Printing out statistics...");
        printTrafficSummaries();
    }

    private void waitForNodesToReportTrafficSummaries() {
        // (Consumer thread) wait for nodes to send their OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY messages
        int numberOfOverlayNodes = this.registrationTable.countEntries();

        try {
            synchronized (trafficSummaryLock) {
                while(overlayNodeReportsTrafficSummaryCounter < numberOfOverlayNodes) {
                    trafficSummaryLock.wait();
                }
            }
        } catch(InterruptedException e) {
            LOG.error("An error occurred while waiting for the overlay nodes to finish reporting their traffic summaries", e);
        }
    }

    private void handleOverlayNodeReportsTrafficSummary(Event event) {
        try {
            OverlayNodeReportsTrafficSummaryHandler handler = new OverlayNodeReportsTrafficSummaryHandler(event);
            int assignedNodeID = handler.getAssignedNodeID();
            int sentPackets = handler.getSentPackets();
            int relayedPackets = handler.getRelayedPackets();
            int receivedPackets = handler.getReceivedPackets();
            long sendSummation = handler.getSendSummation();
            long receiveSummation = handler.getReceiveSummation();

            TrafficSummary summary = new TrafficSummary(sentPackets, receivedPackets, relayedPackets, sendSummation, receiveSummation);
            addTrafficSummaryToTable(assignedNodeID, summary);
            incrementTrafficSummaryCounter();
        } catch (IOException e) {
            LOG.error("Unable to handle OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY message", e);
        }
    }

    private void incrementTrafficSummaryCounter() {
        // (Producer thread) increment the counter when a node reports a traffic summary and notify the consumer thread
        synchronized (trafficSummaryLock) {
            this.overlayNodeReportsTrafficSummaryCounter++;
            trafficSummaryLock.notify();
        }
    }

    private synchronized void addTrafficSummaryToTable(int nodeID, TrafficSummary summary) {
        this.trafficSummaryTable.put(nodeID, summary);
    }

    private void printTrafficSummaries() {
        System.out.printf("%20s%20s%20s%20s%30s%30s\n", "", "Packets Sent", "Packets Received", "Packets Relayed", "Sum Values Sent", "Sum Values Received");
        int[] nodeIDList = registrationTable.getNodeIDList();
        int totalSentPackets = 0;
        int totalReceivedPackets = 0;
        int totalRelayedPackets = 0;
        long totalSendSummation = 0;
        long totalReceiveSummation = 0;
        for (int nodeID : nodeIDList) {
            TrafficSummary summary = getTrafficSummary(nodeID);
            int sentPackets = summary.getSentPackets();
            totalSentPackets += sentPackets;
            int receivedPackets = summary.getReceivedPackets();
            totalReceivedPackets += receivedPackets;
            int relayedPackets = summary.getRelayedPackets();
            totalRelayedPackets += relayedPackets;
            long sendSummation = summary.getSendSummation();
            totalSendSummation += sendSummation;
            long receiveSummation = summary.getReceiveSummation();
            totalReceiveSummation += receiveSummation;
            System.out.printf("%20s%20s%20s%20s%30s%30s\n", "Node " + nodeID, "" + sentPackets, "" + receivedPackets,
                    "" + relayedPackets, "" + sendSummation, "" + receiveSummation);
        }
        System.out.printf("%20s%20s%20s%20s%30s%30s\n", "Sum", "" + totalSentPackets, "" + totalReceivedPackets,
                "" + totalRelayedPackets, "" + totalSendSummation, "" + totalReceiveSummation);
    }

    private synchronized TrafficSummary getTrafficSummary(int nodeID) {
        return this.trafficSummaryTable.get(nodeID);
    }
}
