package cs455.overlay.wireformats;

public interface Event {
    byte getType();
    byte[] getBytes();
}
