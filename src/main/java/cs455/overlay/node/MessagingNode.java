package cs455.overlay.node;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;
import cs455.overlay.transport.TCPSender;

public class MessagingNode {

    private static Logger LOG = LogManager.getLogger(MessagingNode.class);

    public static void main(String[] args) {
        byte[] message = {125, 40, 32, 47};
        try {
            Socket socket = new Socket("127.0.0.1", 5000);
            TCPSender sender = new TCPSender(socket);
            sender.sendData(message);
            socket.close();
        } catch (IOException ioe) {
            LOG.error(ioe.getMessage());
        }
    }
}
