package cs455.overlay.wireformats;

public abstract class Event {
    public abstract byte getType();
    public abstract byte[] getBytes();
}
