package cs455.overlay.wireformats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EventFactory implements Protocol {

    private static Logger LOG = LogManager.getLogger(EventFactory.class);

    public Event factoryMethod(byte[] data) {
        byte type = data[0];
        if (type == OVERLAY_NODE_SENDS_REGISTRATION) {
            LOG.info("Constructing new OVERLAY_NODE_SENDS_REGISTRATION event");
            return new OverlayNodeSendsRegistration();
        }
        else if (type == REGISTRY_REPORTS_REGISTRATION_STATUS) {
            LOG.info("Constructing new REGISTRY_REPORTS_REGISTRATION_STATUS event");
            return new RegistryReportsRegistrationStatus();
        }
        else {
            LOG.warn("Unknown message type received: " + type);
            return new Unknown();
        }
    }
}
