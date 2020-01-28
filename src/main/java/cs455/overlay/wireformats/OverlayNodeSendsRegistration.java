package cs455.overlay.wireformats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class OverlayNodeSendsRegistration implements Event, Protocol {

    private Logger LOG = LogManager.getLogger(OverlayNodeSendsRegistration.class);

    private byte[] address;
    private byte addressLength;
    private int portNumber;

    public OverlayNodeSendsRegistration() {

    }

    public OverlayNodeSendsRegistration(byte[] address, byte addressLength, int portNumber) {
        this.address = address;
        this.addressLength = addressLength;
        this.portNumber = portNumber;
    }

    @Override
    public byte getType() {
        return OVERLAY_NODE_SENDS_REGISTRATION;
    }

    @Override
    public byte[] getBytes() {
        byte[] marshalledBytes = null;
        try {
            ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

            dout.writeByte(this.getType());
            dout.writeByte(this.addressLength);
            dout.write(this.address, 0, this.addressLength);
            dout.writeInt(this.portNumber);

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
