package cs455.overlay.util;

import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.RegistryRequestsTaskInitiate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class RegistryRequestsTaskInitiateHandler {

    private Logger LOG = LogManager.getLogger(RegistryRequestsTaskInitiateHandler.class);

    private int numberOfMessages;

    public RegistryRequestsTaskInitiateHandler(Event event) throws IOException {
        byte[] data = event.getBytes();
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        int type = din.readByte();
        this.numberOfMessages = din.readInt();
        LOG.debug("The number of messages to send = " + numberOfMessages);
        baInputStream.close();
        din.close();
    }

    public int getNumberOfMessages() {
        return numberOfMessages;
    }
}
