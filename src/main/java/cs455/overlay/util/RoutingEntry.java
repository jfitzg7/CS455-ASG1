package cs455.overlay.util;

public class RoutingEntry {

    private final byte[] IPAddress;
    private final int portNumber;
    private final int nodeID;

    public RoutingEntry(byte[] IPAddress, int portNumber, int nodeID) {
        this.IPAddress = IPAddress;
        this.portNumber = portNumber;
        this.nodeID = nodeID;
    }

    public byte[] getIPAddress() {
        return IPAddress;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public int getNodeID() {
        return nodeID;
    }
}
