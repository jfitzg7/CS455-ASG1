package cs455.overlay.wireformats;

public abstract class Event {
    byte[] senderIPAddress;
    public abstract byte getType();
    public abstract byte[] getBytes();

    public byte[] getSenderIPAddress() {
        return senderIPAddress;
    }
}
