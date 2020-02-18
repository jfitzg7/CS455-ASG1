package cs455.overlay.util;

import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.NodeReportsOverlaySetupStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

public class NodeReportsOverlaySetupStatusHandler {

    private Logger LOG = LogManager.getLogger(NodeReportsOverlaySetupStatusHandler.class);

    private int successStatus;
    private byte[] informationString;

    public NodeReportsOverlaySetupStatusHandler(Event event) throws IOException {
        byte[] data = event.getBytes();
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        byte type = din.readByte();
        this.successStatus = din.readInt();
        LOG.debug("Success status = " + this.successStatus);
        byte informationStringLength = din.readByte();
        LOG.debug("Information string length = " + informationStringLength);
        this.informationString = new byte[informationStringLength];
        din.readFully(this.informationString);
        LOG.debug("Information string = " + Arrays.toString(this.informationString));
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
