package cs455.overlay.node;

import cs455.overlay.transport.TCPSender;
import cs455.overlay.util.*;
import cs455.overlay.wireformats.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cs455.overlay.transport.TCPServerThread;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Registry extends Node implements Protocol {

    private static Logger LOG = LogManager.getLogger(Registry.class);

    private RegistrationTable registrationTable;

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
                if (event.getType() == OVERLAY_NODE_SENDS_DEREGISTRATION) {
                    LOG.info("Deregistering messaging node...");
                    LOG.debug("bytes received from the OVERLAY_NODE_SENDS_DEREGISTRATION message: " + Arrays.toString(event.getBytes()));
                    handleOverlayNodeSendsDeregistration(socket, event);
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
}
