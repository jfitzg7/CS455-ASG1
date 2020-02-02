package cs455.overlay.wireformats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class EventFactory implements Protocol {

    private static Logger LOG = LogManager.getLogger(EventFactory.class);

    public Event factoryMethod(byte[] data) {
        try {
            ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
            DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
            byte type = din.readByte();
            if (type == OVERLAY_NODE_SENDS_REGISTRATION) {
                LOG.info("Constructing new OVERLAY_NODE_SENDS_REGISTRATION event");
                byte addressLength = din.readByte();
                byte[] address = new byte[addressLength];
                din.readFully(address);
                int portNumber = din.readInt();
                return new OverlayNodeSendsRegistration(address, addressLength, portNumber);
            } else if (type == REGISTRY_REPORTS_REGISTRATION_STATUS) {
                LOG.info("Constructing new REGISTRY_REPORTS_REGISTRATION_STATUS event");
                int successStatus = din.readInt();
                byte informationStringLength = din.readByte();
                byte[] informationString = new byte[informationStringLength];
                din.readFully(informationString);
                return new RegistryReportsRegistrationStatus(successStatus, informationString);
            } else {
                LOG.warn("Unknown message type received: " + type);
            }
            baInputStream.close();
            din.close();
        } catch (IOException ioe) {
            LOG.error("An IOException occurred while trying to unmarshal the data", ioe);
        }
        //return null if no valid message types are detected.
        return null;
    }
}
