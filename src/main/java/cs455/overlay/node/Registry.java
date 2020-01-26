package cs455.overlay.node;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cs455.overlay.transport.TCPServerThread;

import java.io.IOException;

public class Registry {

    private static Logger LOG = LogManager.getLogger(Registry.class);

    public static void main(String[] args) {
        try {
            TCPServerThread server = new TCPServerThread(5000);
            LOG.info("Starting server thread...");
            (new Thread(server)).start();
        } catch (IOException ioe) {
            LOG.error(ioe.getMessage());
        }
    }
}
