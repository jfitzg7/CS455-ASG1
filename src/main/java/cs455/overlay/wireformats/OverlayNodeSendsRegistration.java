package cs455.overlay.wireformats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class OverlayNodeSendsRegistration extends Event implements Protocol {

    private Logger LOG = LogManager.getLogger(OverlayNodeSendsRegistration.class);

    private byte[] messageIPAddress;
    private byte messageIPAddressLength;
    private int portNumber;

    public OverlayNodeSendsRegistration(byte[] senderIPAddress, byte[] messageIPAddress, byte messageIPAddressLength, int portNumber) {
        this.senderIPAddress = senderIPAddress;
        this.messageIPAddress = messageIPAddress;
        this.messageIPAddressLength = messageIPAddressLength;
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
            dout.writeByte(this.messageIPAddressLength);
            dout.write(this.messageIPAddress, 0, this.messageIPAddressLength);
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
