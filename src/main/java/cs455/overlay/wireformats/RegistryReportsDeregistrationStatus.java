package cs455.overlay.wireformats;

public class RegistryReportsDeregistrationStatus extends Event implements Protocol {

    private int successStatus;
    private byte informationStringLength;
    private byte[] informationString;

    public RegistryReportsDeregistrationStatus(int successStatus, byte informationStringLength, byte[] informationString) {
        this.successStatus = successStatus;
        this.informationStringLength = informationStringLength;
        this.informationString = informationString;
    }

    @Override
    public byte getType() {
        return REGISTRY_REPORTS_DEREGISTRATION_STATUS;
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
