package cs455.overlay.wireformats;

public class RegistryReportsRegistrationStatus implements Event, Protocol {
    @Override
    public byte getType() {
        return REGISTRY_REPORTS_REGISTRATION_STATUS;
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
