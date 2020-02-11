package cs455.overlay.util;

import cs455.overlay.wireformats.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

public class OverlayNodeSendsDataHandler {

    private Logger LOG = LogManager.getLogger(OverlayNodeSendsDataHandler.class);

    private int destinationID;
    private int sourceID;

    private int payload;

    private int[] disseminationTrace;

    public OverlayNodeSendsDataHandler(Event event) throws IOException {
        byte[] data = event.getBytes();
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        byte type = din.readByte();
        this.destinationID = din.readInt();
        LOG.debug("The destination ID = " + this.destinationID);
        this.sourceID = din.readInt();
        LOG.debug("The source ID = " + this.sourceID);
        this.payload = din.readInt();
        LOG.debug("The payload = " + this.payload);
        int disseminationTraceLength = din.readInt();
        this.disseminationTrace = new int[disseminationTraceLength];
        for (int i=0; i < disseminationTraceLength; i++) {
            this.disseminationTrace[i] = din.readInt();
        }
        LOG.debug("The dissemination trace = " + Arrays.toString(this.disseminationTrace));
        baInputStream.close();
        din.close();
    }

    public int getDestinationID() {
        return destinationID;
    }

    public int getSourceID() {
        return sourceID;
    }

    public int getPayload() {
        return payload;
    }

    public int[] getDisseminationTrace() {
        return disseminationTrace;
    }
}
