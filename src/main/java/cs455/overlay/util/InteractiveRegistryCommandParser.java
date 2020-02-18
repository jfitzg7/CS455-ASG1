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
        System.out.print("Enter a registry command: ");
        while(sc.hasNextLine()) {
            String command = sc.nextLine();
            StringTokenizer st = new StringTokenizer(command);
            ArrayList<String> tokenList = new ArrayList<>();
            while (st.hasMoreTokens()) {
                tokenList.add(st.nextToken());
            }
            if (tokenList.size() > 0) {
                if (tokenList.get(0).equals("setup-overlay")) {
                    handleSetupOverlayCommand(tokenList);
                }
                else if (tokenList.get(0).equals("start")) {
                    handleStartCommand(tokenList);
                }
                else if (tokenList.get(0).equals("list-messaging-nodes")) {
                    registry.printRegisteredMessagingNodes();
                }
                else if (tokenList.get(0).equals("list-routing-tables")) {
                    registry.printRoutingTables();
                }
                else {
                    System.out.println("Unknown argument provided");
                }
            }
            System.out.print("Enter a registry command: ");
        }
    }

    private void handleSetupOverlayCommand(ArrayList<String> tokenList) {
        if (tokenList.size() == 2) {
            try {
                int numberOfRoutingTableEntries = Integer.parseInt(tokenList.get(1));
                int numberOfRegisteredNodes = registry.getNumberOfRegisteredMessagingNodes();
                if (checkForEnoughRegisteredNodesToSetupOverlay(numberOfRoutingTableEntries, numberOfRegisteredNodes)) {
                    registry.setupOverlay(numberOfRoutingTableEntries);
                }
                else {
                    int minimumRequiredNodes = (int) Math.pow(2, numberOfRoutingTableEntries - 1) + 1;
                    System.out.println("The minimum required nodes for routing tables of size " + numberOfRoutingTableEntries +
                            " is " + minimumRequiredNodes + ", there are currently " + numberOfRegisteredNodes + " messaging nodes registered");
                }
            } catch (NumberFormatException e) {
                System.out.println("Unable to parse the number-of-routing-table-entries, it must be an integer value");
            }
        }
        else {
        System.out.println("Incorrect number of arguments provided to the setup-overlay command");
        }
    }

    private boolean checkForEnoughRegisteredNodesToSetupOverlay(int numberOfRoutingTableEntries, int numberOfRegisteredNodes) {
        int minimumRequiredNodes = (int) Math.pow(2, numberOfRoutingTableEntries - 1) + 1;
        if (numberOfRegisteredNodes < minimumRequiredNodes) {
            return false;
        }
        else {
            return true;
        }
    }

    private void handleStartCommand(ArrayList<String> tokenList) {
        if (registry.getOverlaySetupSuccessStatus()) {
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
            System.out.println("The overlay has not been successfully established yet!");
        }
    }

}
