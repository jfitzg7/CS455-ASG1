package cs455.overlay.transport;

import cs455.overlay.node.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.*;
import java.io.*;


public class TCPServerThread implements Runnable {

    private static Logger LOG = LogManager.getLogger(TCPServerThread.class);
    private ServerSocket serverSocket;
    private Node node;

    public TCPServerThread(int portNum, Node node) throws IOException {
        this.serverSocket = new ServerSocket(portNum);
        this.node = node;
    }

    @Override
    public void run() {
        LOG.info("Server thread is now running and waiting for client...");
        try {
            while(true) {
                Socket clientSocket = this.serverSocket.accept();
                LOG.info("Received a new connection!");
                LOG.info("starting receiver thread...");
                (new Thread(new TCPReceiverThread(clientSocket, node))).start();
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }
}
