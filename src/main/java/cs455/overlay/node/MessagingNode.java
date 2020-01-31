package cs455.overlay.node;

import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.Protocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

import cs455.overlay.transport.TCPSender;

public class MessagingNode extends Node implements Protocol {

    private static Logger LOG = LogManager.getLogger(MessagingNode.class);
    private int localPort;
    private byte[] localAddress;
    private byte localAddressLength;

    private MessagingNode() {
        this.eventFactory = new EventFactory();
    }

    public static void main(String[] args) {
        try {
            MessagingNode messagingNode = new MessagingNode();
            ServerSocket serverSocket = new ServerSocket(0);
            messagingNode.localPort = serverSocket.getLocalPort();
            LOG.info("Messaging node is listening on port: " + messagingNode.localPort);
            messagingNode.localAddress = serverSocket.getInetAddress().getAddress();
            //converting int to byte is potentially unsafe, might need proper handling.
            messagingNode.localAddressLength = (byte) messagingNode.localAddress.length;
            LOG.info("Messaging node is listening at address: " + Arrays.toString(messagingNode.localAddress)
                    + " length: " + messagingNode.localAddressLength);

            //Start the TCPServerThread with the new ServerSocket
            LOG.info("Starting the TCPServerThread...");
            (new Thread(new TCPServerThread(serverSocket, messagingNode))).start();

            //Construct new OVERLAY_NODE_SENDS_REGISTRATION message
            OverlayNodeSendsRegistration sendsRegistrationRequest = new OverlayNodeSendsRegistration(messagingNode.localAddress, messagingNode.localAddress,
                    messagingNode.localAddressLength, messagingNode.localPort);

            //Send OVERLAY_NODE_SENDS_REGISTRATION message to the registry
            Socket socket = new Socket("127.0.0.1", 5000);
            TCPSender sender = new TCPSender(socket);
            sender.sendData(sendsRegistrationRequest.getBytes());
            socket.close();
        } catch (IOException ioe) {
            LOG.error(ioe.getMessage());
        }
    }

    @Override
    public void onEvent(Event event) {
        if (event != null) {
            try {
                if (event.getType() == REGISTRY_REPORTS_REGISTRATION_STATUS) {
                    LOG.debug("bytes received from the REGISTRY_REPORTS_REGISTRATION_STATUS message: " + Arrays.toString(event.getBytes()));
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
}
