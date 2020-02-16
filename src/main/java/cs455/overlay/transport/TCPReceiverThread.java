package cs455.overlay.transport;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.*;
import java.io.*;
import java.util.Arrays;

public class TCPReceiverThread implements Runnable {

    private static Logger LOG = LogManager.getLogger(TCPReceiverThread.class);
    private Socket socket;
    private DataInputStream din;
    private Node node;

    public TCPReceiverThread(Socket socket, Node node) throws IOException {
        this.socket = socket;
        din = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.node = node;
    }

    @Override
    public void run() {
        int dataLength;
        while(socket != null) {
            try {

                dataLength = din.readInt();

                byte[] data = new byte[dataLength];
                din.readFully(data);
                LOG.debug("Data: " + Arrays.toString(data));
                Event event = node.eventFactory.factoryMethod(data);
                node.onEvent(this.socket, event);
            } catch(SocketException se) {
                LOG.error("SocketException: " + se.getMessage(), se);
                break;
            } catch(IOException ioe) {
                LOG.error("IOException: " + ioe.getMessage(), ioe);
                break;
            }
        }
    }
}
