package cs455.overlay.wireformats;

public class OverlayNodeSendsRegistration implements Event, Protocol {

    @Override
    public byte getType() {
        return OVERLAY_NODE_SENDS_REGISTRATION;
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
