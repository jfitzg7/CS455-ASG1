package cs455.overlay.node;

import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveMessagingNodeCommandParser;
import cs455.overlay.util.RegistryReportsDeregistrationStatusHandler;
import cs455.overlay.util.RegistryReportsRegistrationStatusHandler;
import cs455.overlay.wireformats.*;
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
    private int nodeID;
    private InteractiveMessagingNodeCommandParser commandParser;

    private MessagingNode() {
        this.eventFactory = new EventFactory();
        this.nodeID = -1;
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
}
