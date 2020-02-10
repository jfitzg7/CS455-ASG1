package cs455.overlay.transport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class TCPConnectionCache {

    private Logger LOG = LogManager.getLogger(TCPConnectionCache.class);

    private Map<Integer, TCPConnection> connectionCache;

    public TCPConnectionCache() {
        connectionCache = new HashMap<>();
    }

    public void cacheTCPConnection(int nodeID, TCPConnection connection) {
        connectionCache.put(nodeID, connection);
    }

    public TCPConnection getTCPConnection(int nodeID) {
        return connectionCache.get(nodeID);
    }
}
