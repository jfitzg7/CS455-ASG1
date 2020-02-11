package cs455.overlay.wireformats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class OverlayNodeReportsTaskFinished extends Event implements Protocol {

    private Logger LOG = LogManager.getLogger(OverlayNodeReportsTaskFinished.class);

    private byte[] IPAddress;
    private int portNumber;
    private int nodeID;

    public OverlayNodeReportsTaskFinished(byte[] IPAddress, int portNumber, int nodeID) {
        this.IPAddress = IPAddress;
        this.portNumber = portNumber;
        this.nodeID = nodeID;
    }

    @Override
    public byte getType() {
        return OVERLAY_NODE_REPORTS_TASK_FINISHED;
    }

    @Override
    public byte[] getBytes() {
        byte[] marshalledBytes = null;
        try {
            ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

            dout.writeByte(this.getType());
            dout.writeByte(this.IPAddress.length);
            dout.write(this.IPAddress, 0, this.IPAddress.length);
            dout.writeInt(this.portNumber);
            dout.writeInt(this.nodeID);

            dout.flush();
            marshalledBytes = baOutputStream.toByteArray();
            LOG.info("OVERLAY_NODE_REPORTS_TASK_FINISHED bytes = " + Arrays.toString(marshalledBytes));

            baOutputStream.close();
            dout.close();
        } catch (IOException ioe) {
            LOG.error("OverlayNodeReportsTaskFinished: An exception occurred in getBytes()", ioe);
        }
        return marshalledBytes;
    }
}
