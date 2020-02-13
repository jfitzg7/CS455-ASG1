package cs455.overlay.util;

import cs455.overlay.node.Registry;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class InteractiveRegistryCommandParser implements Runnable {

    Registry registry;

    public InteractiveRegistryCommandParser(Registry registry) {
        this.registry = registry;
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter a command: ");
        while(sc.hasNextLine()) {
            String command = sc.nextLine();
            StringTokenizer st = new StringTokenizer(command);
            ArrayList<String> tokenList = new ArrayList<>();
            while (st.hasMoreTokens()) {
                tokenList.add(st.nextToken());
            }
            if (tokenList.size() > 0) {
                if (tokenList.get(0).equals("setup-overlay")) {
                    if (tokenList.size() == 2) {
                        try {
                            int routingTableSize = Integer.parseInt(tokenList.get(1));
                            registry.setupOverlay(routingTableSize);
                        } catch (NumberFormatException e) {
                            System.out.println("Unable to parse the number-of-routing-table-entries, it must be an integer value");
                        }
                    }
                    else {
                        System.out.println("Incorrect number of arguments provided for setup-overlay command");
                    }
                }
                else if (tokenList.get(0).equals("start")) {
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
            System.out.print("Enter a command: ");
        }
    }
}
