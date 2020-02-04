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
        while(sc.hasNextLine()) {
            String command = sc.nextLine();
            if (command.equals("print-counters-and-diagnostics")) {

            }
            else if (command.equals("exit-overlay")) {
                messagingNode.sendDeregistrationMessage();
                //wait for registry to respond back
                while(!exit);
                break;
            }
            else {
                System.out.println("Unknown command received: " + command);
            }
        }
    }

    public void exit() {
        exit = true;
    }
}
