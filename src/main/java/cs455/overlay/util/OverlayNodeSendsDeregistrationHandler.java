package cs455.overlay.util;

import cs455.overlay.wireformats.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class OverlayNodeSendsDeregistrationHandler {

    private Logger LOG = LogManager.getLogger(OverlayNodeSendsDeregistrationHandler.class);

    private byte IPAddressLength;
    private byte[] IPAddress;
    private int portNumber;
    private int assignedNodeID;

    public OverlayNodeSendsDeregistrationHandler(Event event) throws IOException {
        byte[] data = event.getBytes();
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        byte type = din.readByte();
        this.IPAddressLength = din.readByte();
        LOG.debug("The address length = " + this.IPAddressLength);
        this.IPAddress = new byte[this.IPAddressLength];
        din.readFully(this.IPAddress);
        LOG.debug("The address = " + this.IPAddressLength);
        this.portNumber = din.readInt();
        LOG.debug("The port number = " + this.portNumber);
        this.assignedNodeID = din.readInt();
        LOG.debug("The assigned node ID = " + this.assignedNodeID);
        baInputStream.close();
        din.close();
    }

    public byte[] getIPAddress() {
        return IPAddress;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public int getAssignedNodeID() {
        return assignedNodeID;
    }
}
