package cs455.overlay.util;

import cs455.overlay.node.MessagingNode;
import sun.rmi.runtime.Log;

import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;

public class MessagingNodeInfo {

    private LogicalNetworkAddress networkAddress;
    private Socket socket;

    public MessagingNodeInfo(LogicalNetworkAddress networkAddress, Socket socket) {
        this.networkAddress = networkAddress;
        this.socket = socket;
    }


    public Socket getSocket() {
        return socket;
    }

    public LogicalNetworkAddress getNetworkAddress() {
        return networkAddress;
    }

    @Override
    public boolean equals(Object o) {
        //self check
        if (this == o) {
            return true;
        }

        //null check
        if (o == null) {
            return false;
        }

        //type check and cast
        if (getClass() != o.getClass()) {
            return false;
        }

        MessagingNodeInfo messagingNodeInfo = (MessagingNodeInfo) o;

        //field comparison
        return Objects.equals(networkAddress, messagingNodeInfo.networkAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(networkAddress);
    }

    @Override
    public String toString() {
        return this.networkAddress.toString();
    }
}
