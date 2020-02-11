package cs455.overlay.util;

import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.OverlayNodeReportsTrafficSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class OverlayNodeReportsTrafficSummaryHandler {

    private Logger LOG = LogManager.getLogger(OverlayNodeReportsTrafficSummaryHandler.class);

    private int assignedNodeID;
    private int sentPackets;
    private int relayedPackets;
    private int receivedPackets;
    private long sendSummation;
    private long receiveSummation;

    public OverlayNodeReportsTrafficSummaryHandler(Event event) throws IOException {
        byte[] data = event.getBytes();
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        byte type = din.readByte();
        this.assignedNodeID = din.readInt();
        LOG.debug("Assigned node ID = " + this.assignedNodeID);
        this.sentPackets = din.readInt();
        LOG.debug("Sent packets = " + this.sentPackets);
        this.relayedPackets = din.readInt();
        LOG.debug("Relayed packets = " + this.relayedPackets);
        this.sendSummation = din.readLong();
        LOG.debug("Send summation = " + this.sendSummation);
        this.receivedPackets = din.readInt();
        LOG.debug("Received packets = " + this.receivedPackets);
        this.receiveSummation = din.readLong();
        LOG.debug("Receive summation = " + this.receiveSummation);
        baInputStream.close();
        din.close();
    }

    public int getAssignedNodeID() {
        return assignedNodeID;
    }

    public int getSentPackets() {
        return sentPackets;
    }

    public int getRelayedPackets() {
        return relayedPackets;
    }

    public int getReceivedPackets() {
        return receivedPackets;
    }

    public long getSendSummation() {
        return sendSummation;
    }

    public long getReceiveSummation() {
        return receiveSummation;
    }
}
