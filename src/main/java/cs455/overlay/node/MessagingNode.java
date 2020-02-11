package cs455.overlay.node;

import cs455.overlay.transport.*;
import cs455.overlay.util.*;
import cs455.overlay.wireformats.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MessagingNode extends Node implements Protocol {

    private static Logger LOG = LogManager.getLogger(MessagingNode.class);
    private int listeningPort;
    private byte[] listeningAddress;
    private byte listeningAddressLength;
    private Socket registrySocket;
    private int nodeID;
    private InteractiveMessagingNodeCommandParser commandParser;

    private RoutingTable routingTable;
    private int[] overlayNodeIDList;
    private TCPConnectionCache connectionCache;

    private int sendTracker;
    private int receiveTracker;
    private int relayTracker;
    private long sendSummation;
    private long receiveSummation;

    private MessagingNode() {
        this.eventFactory = new EventFactory();
        this.nodeID = -1;
        this.connectionCache = new TCPConnectionCache();
        this.sendTracker = 0;
        this.receiveTracker = 0;
        this.sendSummation = 0;
        this.receiveSummation = 0;
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            try {
                String registryHost = args[0];
                int registryPort = Integer.parseInt(args[1]);
                MessagingNode messagingNode = new MessagingNode();
                messagingNode.setUpServerSocket();
                messagingNode.establishConnectionWithRegistry(registryHost, registryPort);
                messagingNode.sendRegistrationMessage();

                //start command parser thread
                Thread commandParserThread = messagingNode.startCommandParserThread();
            } catch (NumberFormatException e) {
                LOG.error("Exception occurred in MessageNode main method", e);
            }
        } else {
            System.out.println("Incorrect number of arguments provided");
        }
    }

    private void setUpServerSocket() {
        try {
            ServerSocket serverSocket = new ServerSocket(0);
            this.listeningPort = serverSocket.getLocalPort();
            LOG.info("Messaging node is listening on port: " + this.listeningPort);

            //Start the TCPServerThread with the new ServerSocket
            LOG.info("Starting the TCPServerThread...");
            (new Thread(new TCPServerThread(serverSocket, this))).start();
        } catch (IOException e) {
            LOG.error("An exception occurred while trying to set up the server socket", e);
        }
    }

    private void establishConnectionWithRegistry(String registryIPAddress, int registryPortNumber) {
        try {
            this.registrySocket = new Socket(registryIPAddress, registryPortNumber);
            this.listeningAddress = registrySocket.getLocalAddress().getAddress();
            this.listeningAddressLength = (byte) this.listeningAddress.length;
            LOG.info("Messaging node is listening at address: " + Arrays.toString(this.listeningAddress)
                    + " length: " + this.listeningAddressLength);
        } catch (IOException e) {
            LOG.error("An exception occurred while trying to establish a connection with the registry", e);
        }
    }

    private void sendRegistrationMessage() {
        try {
            OverlayNodeSendsRegistration sendsRegistrationRequest = new OverlayNodeSendsRegistration(this.listeningAddress,
                    this.listeningAddressLength, this.listeningPort);

            //Send OVERLAY_NODE_SENDS_REGISTRATION message to the registry
            (new Thread(new TCPReceiverThread(this.registrySocket, this))).start();
            TCPSender sender = new TCPSender(this.registrySocket);
            sender.sendData(sendsRegistrationRequest.getBytes());
        } catch (IOException e) {
            LOG.error("An exception occurred while attempting to register with the registry node");
        }
    }

    private Thread startCommandParserThread() {
        //start command parser thread
        this.commandParser = new InteractiveMessagingNodeCommandParser(this);
        Thread commandParserThread = new Thread(this.commandParser);
        commandParserThread.start();
        return commandParserThread;
    }

    @Override
    public void onEvent(Socket socket, Event event) {
        if (event != null) {
            try {
                if (event.getType() == REGISTRY_REPORTS_REGISTRATION_STATUS) {
                    LOG.info("Received registration status report from the registry...");
                    LOG.debug("bytes received from the REGISTRY_REPORTS_REGISTRATION_STATUS message: " + Arrays.toString(event.getBytes()));
                    handleRegistryReportsRegistrationStatus(event);
                }
                else if (event.getType() == REGISTRY_REPORTS_DEREGISTRATION_STATUS) {
                    LOG.info("Received deregistration status report from the registry...");
                    LOG.debug("bytes received from the REGISTRY_REPORTS_DEREGISTRATION_STATUS message: " + Arrays.toString(event.getBytes()));
                    handleRegistryReportsDeregistrationStatus(event);
                }
                else if (event.getType() == REGISTRY_SENDS_NODE_MANIFEST) {
                    LOG.info("Received node manifest message from the registry...");
                    LOG.debug("bytes received from the REGISTRY_SENDS_NODE_MANIFEST message: " + Arrays.toString(event.getBytes()));
                    handleRegistrySendsNodeManifest(event);
                }
                else if (event.getType() == REGISTRY_REQUESTS_TASK_INITIATE) {
                    LOG.info("Received task initiate message from the registry...");
                    LOG.debug("bytes received from the REGISTRY_REQUESTS_TASK_INITIATE message: " + Arrays.toString(event.getBytes()));
                    handleRegistryRequestsTaskInitiate(event);
                }
                else if (event.getType() == OVERLAY_NODE_SENDS_DATA) {
                    LOG.info("Received data from an overlay node...");
                    LOG.debug("bytes received from the OVERLAY_NODE_SENDS_DATA message: " + Arrays.toString(event.getBytes()));
                    handleOverlayNodeSendsData(event);
                }
                else if (event.getType() == REGISTRY_REQUESTS_TRAFFIC_SUMMARY) {
                    LOG.info("Received traffic summary request from the registry...");
                    LOG.debug("bytes received from the REGISTRY_REQUESTS_TRAFFIC_SUMMARY message: " + Arrays.toString(event.getBytes()));
                    handleRegistryRequestsTrafficSummary();
                }
                else {
                    LOG.error("Something went wrong while reading the event type in onEvent()");
                }
            } catch (NullPointerException npe) {
                LOG.error("A NullPointerException occurred while trying to get the event type");
            }
        }
        else {
            LOG.warn("The event received is null and will not be handled");
        }
    }

    private void handleRegistryReportsRegistrationStatus(Event event) {
        try {
            RegistryReportsRegistrationStatusHandler handler = new RegistryReportsRegistrationStatusHandler(event);
            int successStatus = handler.getSuccessStatus();
            if (successStatus == -1) {
                LOG.warn("Attempting to register failed, no ID was assigned to this messaging node");
            }
            else {
                LOG.info("The registration attempt succeeded! this messaging node's ID is " + successStatus);
                this.nodeID = successStatus;
            }
            LOG.info("The information string received in registration status report: " + new String(handler.getInformationString()));
        } catch (IOException e) {
            LOG.error("Unable to handle the REGISTRY_REPORTS_REGISTRATION_STATUS message");
        }
    }

    private void handleRegistryReportsDeregistrationStatus(Event event) {
        try {
            RegistryReportsDeregistrationStatusHandler handler = new RegistryReportsDeregistrationStatusHandler(event);
            LOG.debug("Success status = " + handler.getSuccessStatus());
            LOG.debug("Deregistration information string = " + new String(handler.getInformationString()));
            System.exit(0);
        } catch(IOException e) {
            LOG.error("Unable to handle REGISTRY_REPORTS_DEREGISTRATION_STATUS message", e);
            System.exit(-1);
        }

    }

    public void sendDeregistrationMessage() {
        try {
            OverlayNodeSendsDeregistration deregistrationMessage =
                    new OverlayNodeSendsDeregistration(this.listeningAddressLength, this.listeningAddress, this.listeningPort, this.nodeID);

            TCPSender sender = new TCPSender(this.registrySocket);
            sender.sendData(deregistrationMessage.getBytes());
        } catch(IOException e) {
            LOG.error("Unable to send deregistration message to the registry", e);
        }
    }

    private void handleRegistrySendsNodeManifest(Event event) {
        try {
            RegistrySendsNodeManifestHandler handler = new RegistrySendsNodeManifestHandler(event);
            this.routingTable = handler.getRoutingTable();
            this.overlayNodeIDList = handler.getNodeIDList();
            int successStatus = this.nodeID;
            String informationString = "Unable to establish connection to node IDs:";
            for(int nodeID : this.routingTable.getNodeIDList()) {
                try {
                    LOG.info("Trying to establish a connection to messaging node " + nodeID);
                    RoutingEntry entry = this.routingTable.getRoutingEntryByNodeID(nodeID);
                    InetAddress IPAddress = InetAddress.getByAddress(entry.getIPAddress());
                    Socket socket = new Socket(IPAddress, entry.getPortNumber());
                    TCPConnection connection = new TCPConnection(socket, this);
                    this.connectionCache.cacheTCPConnection(nodeID, connection);
                } catch(IOException e) {
                    LOG.error("Unable to establish connection to node " + nodeID, e);
                    successStatus = -1;
                    informationString += " " + nodeID;
                }
            }
            if (successStatus != -1) {
                informationString = "Successfully established a connection to all nodes in the routing table!";
            }

            NodeReportsOverlaySetupStatus setupStatus = new NodeReportsOverlaySetupStatus(successStatus, informationString.getBytes());

            TCPSender sender = new TCPSender(this.registrySocket);
            sender.sendData(setupStatus.getBytes());
        } catch(IOException e) {
            LOG.error("Unable to handle a REGISTRY_SENDS_NODE_MANIFEST message", e);
        }

    }

    private void handleRegistryRequestsTaskInitiate(Event event) {
        try {
            RegistryRequestsTaskInitiateHandler handler = new RegistryRequestsTaskInitiateHandler(event);
            Random rand = new Random();
            int numberOfMessages = handler.getNumberOfMessages();
            int[] nodeIDListWithoutCurrentID = constructNodeIDListWithoutCurrentNodeID();
            for(int i=0; i < numberOfMessages; i++) {
                int randomIndex = rand.nextInt(nodeIDListWithoutCurrentID.length);
                int randomNodeID = nodeIDListWithoutCurrentID[randomIndex];
                LOG.debug("Randomly chosen destination node ID = " + randomNodeID);
                int payload = (int) ThreadLocalRandom.current().nextLong(Integer.MIN_VALUE, (long) Integer.MAX_VALUE + 1);
                LOG.debug("Randomly chosen payload = " + payload);
                int[] disseminationTrace = new int[0];
                OverlayNodeSendsData data = new OverlayNodeSendsData(randomNodeID, this.nodeID, payload, disseminationTrace);
                int nextNodeID = selectNodeToSendDataTo(randomNodeID);
                LOG.info("Attempting to send data to node " + nextNodeID);
                TCPConnection connection = this.connectionCache.getTCPConnection(nextNodeID);
                try {
                    connection.sendData(data.getBytes());
                    this.sendTracker++;
                    this.sendSummation += payload;
                    LOG.debug("sendTracker = " + this.sendTracker + " sendSummation = " + this.sendSummation);
                } catch (IOException e) {
                    LOG.error("Unable to send OVERLAY_NODE_SENDS_DATA message to node " + nextNodeID, e);
                }

            }
            //send task finished message back to the registry
            sendOverlayNodeReportsTaskFinishedMessage();

        } catch(IOException e) {
            LOG.error("Unable to handle REGISTRY_REQUESTS_TASK_INITIATE message", e);
        }
    }

    private int[] constructNodeIDListWithoutCurrentNodeID() {
        int[] newNodeIDList = new int[this.overlayNodeIDList.length - 1];
        int newNodeIDListCounter = 0;
        for (int i=0; i < this.overlayNodeIDList.length; i++) {
            if (this.overlayNodeIDList[i] != this.nodeID) {
                newNodeIDList[newNodeIDListCounter] = this.overlayNodeIDList[i];
                newNodeIDListCounter++;
            }
        }
        return newNodeIDList;
    }

    private void sendOverlayNodeReportsTaskFinishedMessage() {
        OverlayNodeReportsTaskFinished taskFinished = new OverlayNodeReportsTaskFinished(this.listeningAddress, this.listeningPort, this.nodeID);
        try {
            TCPSender sender = new TCPSender(this.registrySocket);
            sender.sendData(taskFinished.getBytes());
        } catch(IOException e) {
            LOG.error("Unable to send OVERLAY_NODE_REPORTS_TASK_FINISHED message", e);
        }
    }

    private void handleOverlayNodeSendsData(Event event) {
        try {
            OverlayNodeSendsDataHandler handler = new OverlayNodeSendsDataHandler(event);
            if (handler.getDestinationID() == this.nodeID) {
                LOG.info("Received an OVERLAY_NODE_SENDS_DATA message destined for this node!");
                incrementReceiveTracker();
                addToReceiveSummation(handler.getPayload());
            }
            else {
                int[] currentDisseminationTrace = handler.getDisseminationTrace();
                int[] newDisseminationTrace = new int[currentDisseminationTrace.length + 1];
                //copy current dissemination trace to the new one
                for (int i=0; i < currentDisseminationTrace.length; i++) {
                    newDisseminationTrace[i] = currentDisseminationTrace[i];
                }
                //add this messaging nodes ID to the dissemination trace
                newDisseminationTrace[newDisseminationTrace.length - 1] = this.nodeID;
                OverlayNodeSendsData data = new OverlayNodeSendsData(handler.getDestinationID(), handler.getSourceID(),
                        handler.getPayload(), newDisseminationTrace);
                int nextNodeID = selectNodeToSendDataTo(handler.getDestinationID());
                LOG.info("Attempting to relay data to node " + nextNodeID);
                TCPConnection connection = this.connectionCache.getTCPConnection(nextNodeID);
                try {
                    connection.sendData(data.getBytes());
                    incrementRelayTracker();
                } catch (IOException e) {
                    LOG.error("Unable to relay OVERLAY_NODE_SENDS_DATA message to node " + nextNodeID);
                }
            }
        } catch (IOException e) {
            LOG.error("Unable to handle OVERLAY_NODE_SENDS_DATA message", e);
        }

    }

    private synchronized void incrementReceiveTracker() {
        this.receiveTracker++;
        LOG.debug("Receive tracker = " + this.receiveTracker);
    }

    private synchronized void addToReceiveSummation(int payload) {
        this.receiveSummation += payload;
        LOG.debug("Receive summation = " + this.receiveSummation);
    }

    private synchronized void incrementRelayTracker() {
        this.relayTracker++;
        LOG.debug("Relay tracker = " + this.relayTracker);
    }

    private int selectNodeToSendDataTo(int destinationID) {
        int[] routingTableNodeIDs = this.routingTable.getNodeIDListSortedByHopsAway();
        int bestChoiceID = -1;
        int minimumHopsAway = Integer.MAX_VALUE;
        for (int i=0; i < routingTableNodeIDs.length; i++) {
            int hopsAway = findHopsAwayFromNodeToDestination(routingTableNodeIDs[i], destinationID);
            if(hopsAway < minimumHopsAway) {
                minimumHopsAway = hopsAway;
                bestChoiceID = routingTableNodeIDs[i];
            }
        }
        return bestChoiceID;
    }

    private int findHopsAwayFromNodeToDestination(int startingID, int destinationID) {
        //TODO throw IDNotFoundException?

        int startingIndex = -1;
        int destinationIndex = -1;

        //find starting index
        for (int i=0; i < this.overlayNodeIDList.length; i++) {
            if (this.overlayNodeIDList[i] == startingID) {
                startingIndex = i;
            }
        }

        //find destination index
        for (int i=0; i < this.overlayNodeIDList.length; i++) {
            if (this.overlayNodeIDList[i] == destinationID) {
                destinationIndex = i;
            }
        }

        if (startingIndex > destinationIndex) {
            return (this.overlayNodeIDList.length - startingIndex) + destinationIndex;
        }
        else {
            return destinationIndex - startingIndex;
        }
    }

    private void handleRegistryRequestsTrafficSummary() {
        OverlayNodeReportsTrafficSummary trafficSummary = new OverlayNodeReportsTrafficSummary(this.nodeID, this.sendTracker,
                this.relayTracker, this.receiveTracker, this.sendSummation, this.receiveSummation);

        try {
            TCPSender sender = new TCPSender(this.registrySocket);
            sender.sendData(trafficSummary.getBytes());
        } catch (IOException e) {
            LOG.error("Unable to send OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY message to the registry", e);
        }
    }

    public int getSendTracker() {
        return sendTracker;
    }

    public int getReceiveTracker() {
        return receiveTracker;
    }

    public int getRelayTracker() {
        return relayTracker;
    }

    public long getSendSummation() {
        return sendSummation;
    }

    public long getReceiveSummation() {
        return receiveSummation;
    }

    public int getNodeID() {
        return nodeID;
    }
}
