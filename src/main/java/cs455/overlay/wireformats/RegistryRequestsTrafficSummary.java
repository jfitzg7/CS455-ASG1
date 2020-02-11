package cs455.overlay.wireformats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class RegistryRequestsTrafficSummary extends Event implements Protocol {

    private Logger LOG = LogManager.getLogger(RegistryRequestsTrafficSummary.class);

    @Override
    public byte getType() {
        return REGISTRY_REQUESTS_TRAFFIC_SUMMARY;
    }

    @Override
    public byte[] getBytes() {
        byte[] marshalledBytes = null;
        try {
            ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

            dout.writeByte(this.getType());

            dout.flush();
            marshalledBytes = baOutputStream.toByteArray();
            LOG.info("marshalled bytes = " + Arrays.toString(marshalledBytes));

            baOutputStream.close();
            dout.close();
        } catch (IOException ioe) {
            LOG.error("IOException encountered while trying to construct REGISTRY_REQUESTS_TASK_INITIATE message", ioe);
        }
        return marshalledBytes;
    }
}
