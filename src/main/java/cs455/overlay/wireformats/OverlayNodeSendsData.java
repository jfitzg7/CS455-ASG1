package cs455.overlay.wireformats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class OverlayNodeSendsData extends Event implements Protocol {

    private Logger LOG = LogManager.getLogger(OverlayNodeSendsData.class);

    private int destinationID;
    private int sourceID;

    private int payload;

    private int[] disseminationTrace;

    public OverlayNodeSendsData(int destinationID, int sourceID, int payload, int[] disseminationTrace) {
        this.destinationID = destinationID;
        this.sourceID = sourceID;
        this.payload = payload;
        this.disseminationTrace = disseminationTrace;
    }

    @Override
    public byte getType() {
        return OVERLAY_NODE_SENDS_DATA;
    }

    @Override
    public byte[] getBytes() {
        byte[] marshalledBytes = null;
        try {
            ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

            dout.writeByte(this.getType());
            dout.writeInt(this.destinationID);
            dout.writeInt(this.sourceID);
            dout.writeInt(this.payload);

            dout.writeInt(this.disseminationTrace.length);
            for (int i=0; i < this.disseminationTrace.length; i++) {
                dout.writeInt(this.disseminationTrace[i]);
            }

            dout.flush();
            marshalledBytes = baOutputStream.toByteArray();
            LOG.debug("OVERLAY_NODE_SENDS_DATA bytes = " + Arrays.toString(marshalledBytes));

            baOutputStream.close();
            dout.close();
        } catch (IOException ioe) {
            LOG.error("An exception occurred while constructing the byte array", ioe);
        }
        return marshalledBytes;
    }
}
