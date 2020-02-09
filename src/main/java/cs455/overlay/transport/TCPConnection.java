package cs455.overlay.transport;

import cs455.overlay.node.Node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class TCPConnection {

    private Socket socket;
    private TCPReceiverThread receiverThread;
    private TCPSender sender;

    public TCPConnection(Socket socket, Node node) throws IOException {
        this.socket = socket;
        this.receiverThread = new TCPReceiverThread(socket, node);
        (new Thread(this.receiverThread)).start();
        this.sender = new TCPSender(socket);
    }

    public InetAddress getRemoteAddress() {
        return this.socket.getInetAddress();
    }

    public int getRemotePort() {
        return this.socket.getPort();
    }

    public InetAddress getLocalAddress() {
        return this.socket.getLocalAddress();
    }

    public int getLocalPort() {
        return this.socket.getLocalPort();
    }
}
