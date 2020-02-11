package cs455.overlay.wireformats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class OverlayNodeReportsTrafficSummary extends Event implements Protocol {

    private Logger LOG = LogManager.getLogger(OverlayNodeReportsTrafficSummary.class);

    private int assignedNodeID;
    private int sentPackets;
    private int relayedPackets;
    private int receivedPackets;
    private long sendSummation;
    private long receiveSummation;

    public OverlayNodeReportsTrafficSummary(int assignedNodeID, int sentPackets, int relayedPackets, int receivedPackets, long sendSummation, long receiveSummation) {
        this.assignedNodeID = assignedNodeID;
        this.sentPackets = sentPackets;
        this.relayedPackets = relayedPackets;
        this.receivedPackets = receivedPackets;
        this.sendSummation = sendSummation;
        this.receiveSummation = receiveSummation;
    }

    @Override
    public byte getType() {
        return OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY;
    }

    @Override
    public byte[] getBytes() {
        byte[] marshalledBytes = null;
        try {
            ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

            dout.writeByte(this.getType());
            dout.writeInt(this.assignedNodeID);
            dout.writeInt(this.sentPackets);
            dout.writeInt(this.relayedPackets);
            dout.writeLong(this.sendSummation);
            dout.writeInt(this.receivedPackets);
            dout.writeLong(this.receiveSummation);

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
