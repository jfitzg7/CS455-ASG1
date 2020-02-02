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
        /* https://stackoverflow.com/questions/38339542/java-io-ioexception-mark-reset-not-supported
         * Had to wrap the socket.getInputStream() in BufferedInputStream to avoid mark/reset not supported error
         * in the run method.
         */
        din = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.node = node;
    }

    @Override
    public void run() {
        int dataLength;
        while(socket != null) {
            try {
                /* https://techtavern.wordpress.com/2010/11/09/how-to-prevent-eofexception-when-socket-is-closed/
                 * Above link describes how to avoid an EOFException if a socket is closed. I only want an
                 * IOException to occur if there is some sort of communication failure.
                 */

                din.mark(1);
                if (din.read() == -1) {
                    LOG.info("The Socket was closed by the sender.");
                    break;
                }
                din.reset();

                dataLength = din.readInt();

                byte[] data = new byte[dataLength];
                din.readFully(data, 0, dataLength);
                LOG.debug("Data: " + Arrays.toString(data));
                Event event = node.eventFactory.factoryMethod(data);
                node.onEvent(this.socket, event);
            } catch(SocketException se) {
                LOG.error("SocketException: " + se.getMessage());
                break;
            } catch(IOException ioe) {
                ioe.printStackTrace();
                LOG.error("IOException: " + ioe.getMessage());
                break;
            }
        }
    }
}
