package cs455.overlay.node;

import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.Protocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cs455.overlay.transport.TCPServerThread;

import java.io.IOException;
import java.util.Arrays;

public class Registry extends Node implements Protocol {

    private static Logger LOG = LogManager.getLogger(Registry.class);

    private Registry() {
        this.eventFactory = new EventFactory();
    }

    public static void main(String[] args) {
        try {
            Registry registry = new Registry();
            TCPServerThread server = new TCPServerThread(5000, registry);
            LOG.info("Starting server thread...");
            (new Thread(server)).start();
        } catch (IOException ioe) {
            LOG.error(ioe.getMessage());
        }
    }

    @Override
    public void onEvent(Event event) {
        if (event != null) {
            try {
                if (event.getType() == OVERLAY_NODE_SENDS_REGISTRATION) {
                    LOG.info("Registering new messaging node...");
                    LOG.info("bytes received from the OVERLAY_NODE_SENDS_REGISTRATION message: " + Arrays.toString(event.getBytes()));
                } else {
                    LOG.error("Something went wrong while reading the event type in onEvent()");
                }
            } catch (NullPointerException npe) {
                LOG.error("A NullPointerException occurred while trying to get the event type");
            }
        }
        else {
            LOG.warn("The event received is null and will not be handled");
        }
    }
}
