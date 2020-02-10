package cs455.overlay.wireformats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class NodeReportsOverlaySetupStatus extends Event implements Protocol {

    private Logger LOG = LogManager.getLogger(NodeReportsOverlaySetupStatus.class);

    private int successStatus;
    private byte[] informationString;

    public NodeReportsOverlaySetupStatus(int successStatus, byte[] informationString) {
        this.successStatus = successStatus;
        this.informationString = informationString;
    }

    @Override
    public byte getType() {
        return NODE_REPORTS_OVERLAY_SETUP_STATUS;
    }

    @Override
    public byte[] getBytes() {
        byte[] marshalledBytes = null;
        try {
            ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

            dout.writeByte(this.getType());
            dout.writeInt(this.successStatus);
            dout.writeByte(this.informationString.length);
            dout.write(this.informationString, 0, this.informationString.length);

            dout.flush();
            marshalledBytes = baOutputStream.toByteArray();
            LOG.info("marshalled bytes = " + Arrays.toString(marshalledBytes));

            baOutputStream.close();
            dout.close();
        } catch (IOException ioe) {
            LOG.error("IOException encountered while trying to write the type", ioe);
        }
        return marshalledBytes;
    }
}
