package cs455.overlay.wireformats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class OverlayNodeSendsDeregistration extends Event implements Protocol{

    private Logger LOG = LogManager.getLogger(OverlayNodeSendsDeregistration.class);

    private byte IPAddressLength;
    private byte[] IPAddress;
    private int portNumber;
    private int assignedNodeID;

    public OverlayNodeSendsDeregistration(byte IPAddressLength, byte[] IPAddress, int portNumber, int assignedNodeID) {
        this.IPAddressLength = IPAddressLength;
        this.IPAddress = IPAddress;
        this.portNumber = portNumber;
        this.assignedNodeID = assignedNodeID;
    }

    @Override
    public byte getType() {
        return OVERLAY_NODE_SENDS_DEREGISTRATION;
    }

    @Override
    public byte[] getBytes() {
        byte[] marshalledBytes = null;
        try {
            ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

            dout.writeByte(this.getType());
            dout.writeByte(this.IPAddressLength);
            dout.write(this.IPAddress, 0, this.IPAddressLength);
            dout.writeInt(this.portNumber);
            dout.writeInt(this.assignedNodeID);

            dout.flush();
            marshalledBytes = baOutputStream.toByteArray();
            LOG.info("OVERLAY_NODE_SENDS_DEREGISTRATION bytes = " + Arrays.toString(marshalledBytes));

            baOutputStream.close();
            dout.close();
        } catch (IOException ioe) {
            LOG.error("OverlayNodeSendsDeregistration: An exception occurred in getBytes()", ioe);
        }
        return marshalledBytes;
    }
}
