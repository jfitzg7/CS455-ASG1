package cs455.overlay.node;

import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
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
    private int listeningPort;
    private byte[] listeningAddress;
    private byte listeningAddressLength;
    private Socket registrySocket;

    private MessagingNode() {
        this.eventFactory = new EventFactory();
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
                commandParserThread.join();
            } catch (InterruptedException e) {
                LOG.error("Exception occurred in MessageNode main method", e);
            }
        } else {
            System.out.println("Incorrect number of arguments provided");
        }
    }

    @Override
    public void onEvent(Socket socket, Event event) {
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

    public void establishConnectionWithRegistry(String registryIPAddress, int registryPortNumber) {
        try {
            this.registrySocket = new Socket(registryIPAddress, registryPortNumber);
            this.listeningAddress = registrySocket.getLocalAddress().getAddress();
            //converting int to byte is potentially unsafe? might need proper handling.
            this.listeningAddressLength = (byte) this.listeningAddress.length;
            LOG.info("Messaging node is listening at address: " + Arrays.toString(this.listeningAddress)
                    + " length: " + this.listeningAddressLength);
        } catch (IOException e) {
            LOG.error("An exception occurred while trying to establish a connection with the registry", e);
        }
    }

    public void setUpServerSocket() {
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

    public void sendRegistrationMessage() {
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

    public Thread startCommandParserThread() {
        //start command parser thread
        Thread commandParserThread = new Thread(new InteractiveCommandParser());
        commandParserThread.start();
        return commandParserThread;
    }
}
