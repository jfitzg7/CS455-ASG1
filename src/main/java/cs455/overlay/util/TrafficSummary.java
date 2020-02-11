package cs455.overlay.util;

public class TrafficSummary {
    private int sentPackets;
    private int receivedPackets;
    private int relayedPackets;
    private long sendSummation;
    private long receiveSummation;

    public TrafficSummary(int sentPackets, int receivedPackets, int relayedPackets, long sendSummation, long receiveSummation) {
        this.sentPackets = sentPackets;
        this.receivedPackets = receivedPackets;
        this.relayedPackets = relayedPackets;
        this.sendSummation = sendSummation;
        this.receiveSummation = receiveSummation;
    }

    public int getSentPackets() {
        return sentPackets;
    }

    public int getReceivedPackets() {
        return receivedPackets;
    }

    public int getRelayedPackets() {
        return relayedPackets;
    }

    public long getSendSummation() {
        return sendSummation;
    }

    public long getReceiveSummation() {
        return receiveSummation;
    }
}
