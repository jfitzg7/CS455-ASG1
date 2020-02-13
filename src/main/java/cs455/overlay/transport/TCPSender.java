package cs455.overlay.transport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.*;
import java.io.*;
import java.util.Arrays;

public class TCPSender {

    private static Logger LOG = LogManager.getLogger(TCPReceiverThread.class);
    private Socket socket;
    private DataOutputStream dout;

    public TCPSender(Socket socket) throws IOException {
        this.socket = socket;
        dout = new DataOutputStream(socket.getOutputStream());
    }

    public synchronized void sendData(byte[] dataToSend) throws IOException {
        int dataLength = dataToSend.length;
        if (dataLength == 0) {
            LOG.warn("Sending a byte array with nothing in it!");
        }
        LOG.info("Length of data being sent: " + dataLength);
        LOG.info("Data being sent: " + Arrays.toString(dataToSend));
        dout.writeInt(dataLength);
        dout.write(dataToSend, 0, dataLength);
        dout.flush();
    }

}
