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
