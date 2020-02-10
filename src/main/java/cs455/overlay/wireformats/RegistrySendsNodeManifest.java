package cs455.overlay.wireformats;

import cs455.overlay.util.RoutingEntry;
import cs455.overlay.util.RoutingTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class RegistrySendsNodeManifest extends Event implements Protocol {

    private Logger LOG = LogManager.getLogger(RegistrySendsNodeManifest.class);

    private byte routingTableSize;
    private RoutingTable routingTable;
    private int[] nodeIDList;

    public RegistrySendsNodeManifest(byte routingTableSize, RoutingTable routingTable, int[] nodeIDList) {
        this.routingTableSize = routingTableSize;
        this.routingTable = routingTable;
        this.nodeIDList = nodeIDList;
    }

    @Override
    public byte getType() {
        return REGISTRY_SENDS_NODE_MANIFEST;
    }

    @Override
    public byte[] getBytes() {
        byte[] marshalledBytes = null;
        try {
            ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

            dout.writeByte(this.getType());
            dout.writeByte(this.routingTableSize);
            for (int i=0; i < routingTableSize; i++) {
                int hopsAway = (int) Math.pow(2, i);
                RoutingEntry nextNode = routingTable.getEntryByHopsAway(hopsAway);
                dout.writeInt(nextNode.getNodeID());
                dout.writeByte(nextNode.getIPAddress().length);
                dout.write(nextNode.getIPAddress(), 0, nextNode.getIPAddress().length);
                dout.writeInt(nextNode.getPortNumber());
            }
            dout.writeByte(nodeIDList.length);
            for (int nodeID : nodeIDList) {
                dout.writeInt(nodeID);
            }

            dout.flush();
            marshalledBytes = baOutputStream.toByteArray();
            LOG.info("marshalled bytes = " + Arrays.toString(marshalledBytes));

            baOutputStream.close();
            dout.close();
        } catch (IOException ioe) {
            LOG.error("IOException encountered while trying to write the type", ioe);
        }
        return marshalledBytes;
    }
}
