package cs455.overlay.util;

import cs455.overlay.wireformats.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

public class RegistrySendsNodeManifestHandler {

    private Logger LOG = LogManager.getLogger(RegistrySendsNodeManifestHandler.class);

    private byte type;
    private byte routingTableSize;
    private RoutingTable routingTable;
    private int[] nodeIDList;

    public RegistrySendsNodeManifestHandler(Event event) throws IOException {
        byte[] data = event.getBytes();
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        this.type = din.readByte();
        this.routingTableSize = din.readByte();
        LOG.debug("Routing table size = " + this.routingTableSize);
        this.routingTable = new RoutingTable();
        for(int i=0; i < routingTableSize; i++) {
            int hopsAway = (int) Math.pow(2, i);
            int nodeID = din.readInt();
            byte IPAddressLength = din.readByte();
            byte[] IPAddress = new byte[IPAddressLength];
            din.readFully(IPAddress);
            int portNumber = din.readInt();
            RoutingEntry entry = new RoutingEntry(IPAddress, portNumber, nodeID, hopsAway);
            routingTable.addRoutingEntry(entry);
        }
        byte nodeIDListSize = din.readByte();
        LOG.debug("Node ID list size = " + nodeIDListSize);
        this.nodeIDList = new int[nodeIDListSize];
        for (int i=0; i < nodeIDListSize; i++) {
            nodeIDList[i] = din.readInt();
        }
        LOG.debug("Node ID list = " + Arrays.toString(this.nodeIDList));
    }

    public byte getRoutingTableSize() {
        return routingTableSize;
    }

    public RoutingTable getRoutingTable() {
        return routingTable;
    }

    public int[] getNodeIDList() {
        return nodeIDList;
    }
}
