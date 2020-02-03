package cs455.overlay.util;

import cs455.overlay.wireformats.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class RegistryReportsRegistrationStatusHandler {

    private Logger LOG = LogManager.getLogger(RegistryReportsRegistrationStatusHandler.class);

    private byte type;
    private int successStatus;
    private byte informationStringLength;
    private byte[] informationString;

    public RegistryReportsRegistrationStatusHandler(Event event) throws IOException {
        byte[] data = event.getBytes();
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        this.type = din.readByte();
        this.successStatus = din.readInt();
        LOG.debug("The success status = " + this.successStatus);
        this.informationStringLength = din.readByte();
        LOG.debug("The length of the information string = " + this.informationStringLength);
        this.informationString = new byte[this.informationStringLength];
        din.readFully(this.informationString);
        LOG.debug("The information string = " + new String(this.informationString));
        baInputStream.close();
        din.close();
    }

    public int getSuccessStatus() {
        return successStatus;
    }

    public byte[] getInformationString() {
        return informationString;
    }
}
