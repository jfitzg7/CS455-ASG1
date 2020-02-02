package cs455.overlay.node;

import cs455.overlay.transport.TCPSender;
import cs455.overlay.util.LogicalNetworkAddress;
import cs455.overlay.util.RegistrationTable;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
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

    private byte[] listeningAddress;

    private Registry() {
        this.eventFactory = new EventFactory();
        this.registrationTable = new RegistrationTable();
    }

    public static void main(String[] args) {
        try {
            Registry registry = new Registry();
            ServerSocket serverSocket = new ServerSocket(5000);
            registry.listeningAddress = serverSocket.getInetAddress().getAddress();

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
                } else {
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

    public void handleOverlayNodeSendsRegistration(Socket socket, Event event) {
        byte[] data = event.getBytes();
        try {
            ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
            DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
            byte type = din.readByte();
            byte addressLength = din.readByte();
            LOG.debug("handleOverlayNodeSendsRegistration: The address length = " + addressLength);
            byte [] address = new byte[addressLength];
            din.readFully(address);
            LOG.debug("handleOverlayNodeSendsRegistration: The address = " + Arrays.toString(address));
            int portNumber = din.readInt();
            LOG.debug("handleOverlayNodeSendsRegistration: the port number = " + portNumber);

            /* TODO NOTE:
             * I should probably add custom exceptions (mismatched or already registered)
             * to handle errors when adding new entries! returning boolean doesn't give
             * enough information to handle properly.
             */
            LOG.info("handleOverlayNodeSendsRegistration: Attempting to add new MessagingNode to the registration table...");
            LogicalNetworkAddress logicalAddress = new LogicalNetworkAddress(address, portNumber);
            int successStatus = -1;
            String informationString = "";
            if (Arrays.equals(address, socket.getInetAddress().getAddress())) {
                if (registrationTable.addNewEntry(logicalAddress)) {
                    successStatus = registrationTable.getID(logicalAddress);
                    informationString = "GREAT SUCCESS!";
                } else {
                    // TODO this needs to be more specific, i.e. make new exceptions to handle specific cases
                    LOG.warn("handleOverlayNodeSendsRegistration: Failed to add MessagingNode to the table");
                    informationString = "Unable to add the MessagingNode to the registration table";
                }
            } else {
                LOG.warn("handleOverlayNodeSendsRegistration: Unable to register the MessagingNode because the IP addresses " +
                        "don't match. address in message: " + Arrays.toString(address) + " address of socket: " +
                        Arrays.toString(socket.getInetAddress().getAddress()));
                informationString = "The IP address in the message does not match the actual IP address of the sender";
            }

            //Set up a socket to send back a REGISTRATION_REPORTS_REGISTRATION_STATUS message
            TCPSender sender = new TCPSender(socket);

            //Construct new RegistrationReportsRegistrationStatus message
            RegistryReportsRegistrationStatus sendRegistrationReply = new RegistryReportsRegistrationStatus(successStatus, informationString.getBytes());

            sender.sendData(sendRegistrationReply.getBytes());

        } catch (IOException ioe) {
            LOG.error("IOException occurred while trying to handle an OVERLAY_NODE_SENDS_REGISTRATION message", ioe);
        }
    }
}
