package cs455.overlay.util;

import cs455.overlay.wireformats.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

public class OverlayNodeSendsRegistrationHandler {

    private Logger LOG = LogManager.getLogger(OverlayNodeSendsRegistrationHandler.class);

    private byte type;
    private byte IPAddressLength;
    private byte[] IPAddress;
    private int portNumber;

    public OverlayNodeSendsRegistrationHandler(Event event) throws IOException{
        byte[] data = event.getBytes();
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        this.type = din.readByte();
        this.IPAddressLength = din.readByte();
        LOG.debug("The address length = " + this.IPAddressLength);
        this.IPAddress = new byte[this.IPAddressLength];
        din.readFully(this.IPAddress);
        LOG.debug("The address = " + Arrays.toString(this.IPAddress));
        this.portNumber = din.readInt();
        LOG.debug("the port number = " + this.portNumber);
        baInputStream.close();
        din.close();
    }

    public byte[] getIPAddress() {
        return IPAddress;
    }

    public int getPortNumber() {
        return portNumber;
    }
}
