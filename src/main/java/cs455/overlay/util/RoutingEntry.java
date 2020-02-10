package cs455.overlay.util;

public class RoutingEntry {

    private final byte[] IPAddress;
    private final int portNumber;
    private final int nodeID;
    private final int hopsAway;

    public RoutingEntry(byte[] IPAddress, int portNumber, int nodeID, int hopsAway) {
        this.IPAddress = IPAddress;
        this.portNumber = portNumber;
        this.nodeID = nodeID;
        this.hopsAway = hopsAway;
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

    public int getHopsAway() {
        return hopsAway;
    }
}
