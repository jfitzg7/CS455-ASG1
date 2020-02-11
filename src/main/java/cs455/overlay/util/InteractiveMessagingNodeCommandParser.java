package cs455.overlay.util;

import cs455.overlay.node.MessagingNode;

import java.util.Scanner;

public class InteractiveMessagingNodeCommandParser implements Runnable {

    private MessagingNode messagingNode;
    private volatile boolean exit = false;

    public InteractiveMessagingNodeCommandParser(MessagingNode messagingNode) {
        this.messagingNode = messagingNode;
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter a command: ");
        while(sc.hasNextLine()) {
            String command = sc.nextLine();
            if (command.equals("print-counters-and-diagnostics")) {
                int nodeID = messagingNode.getNodeID();
                int sendTracker = messagingNode.getSendTracker();
                int receiveTracker = messagingNode.getReceiveTracker();
                int relayTracker = messagingNode.getRelayTracker();
                long sendSummation = messagingNode.getSendSummation();
                long receiveSummation = messagingNode.getReceiveSummation();
                System.out.println("Diagnostics for node " + nodeID + ":");
                System.out.println("    Sent messages = " + sendTracker);
                System.out.println("    Received messages = " + receiveTracker);
                System.out.println("    Relay tracker = " + relayTracker);
                System.out.println("    Send summation = " + sendSummation);
                System.out.println("    Receive summation = " + receiveSummation);
            }
            else if (command.equals("exit-overlay")) {
                messagingNode.sendDeregistrationMessage();
            }
            else {
                System.out.println("Unknown command received: " + command);
            }
            System.out.print("Enter a command: ");
        }
    }

    public void exit() {
        exit = true;
    }
}
