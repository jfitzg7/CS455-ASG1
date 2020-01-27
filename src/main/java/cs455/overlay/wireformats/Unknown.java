package cs455.overlay.wireformats;

public class Unknown implements Event, Protocol {
    @Override
    public byte getType() {
        return UNKNOWN;
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
