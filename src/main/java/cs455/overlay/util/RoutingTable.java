package cs455.overlay.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class RoutingTable {

    private Logger LOG = LogManager.getLogger(RoutingTable.class);

    private ArrayList<RoutingEntry> routingTable;

    public RoutingTable() {
        routingTable = new ArrayList<>();
    }

    public void addRoutingEntry(RoutingEntry entry) {
        routingTable.add(entry);
    }

    public RoutingEntry getRoutingEntryByNodeID(int nodeID) {
        for (RoutingEntry entry : routingTable) {
            if (entry.getNodeID() == nodeID) {
                return entry;
            }
        }
        return null;
    }

    public int[] getNodeIDList() {
        int[] nodeIDList = new int[routingTable.size()];
        int listCounter = 0;
        for (RoutingEntry entry : routingTable) {
            nodeIDList[listCounter] = entry.getNodeID();
            listCounter++;
        }
        return nodeIDList;
    }

    public int[] getNodeIDListSortedByHopsAway() {
        int routingTableSize = this.routingTable.size();
        int[] nodeIDListSortedByHopsAway = new int[routingTableSize];
        for (int i=0; i < routingTableSize; i++) {
            int hopsAway = (int) Math.pow(2, i);
            nodeIDListSortedByHopsAway[i] = getEntryByHopsAway(hopsAway).getNodeID();
        }
        return nodeIDListSortedByHopsAway;
    }

    public RoutingEntry getEntryByHopsAway(int hops) {
        for (RoutingEntry entry : routingTable) {
            if (entry.getHopsAway() == hops) {
                return entry;
            }
        }
        return null;
    }
}
