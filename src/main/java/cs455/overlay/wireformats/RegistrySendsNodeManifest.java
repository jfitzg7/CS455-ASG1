package cs455.overlay.wireformats;

import cs455.overlay.util.RoutingTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistrySendsNodeManifest extends Event implements Protocol {

    private Logger LOG = LogManager.getLogger(RegistrySendsNodeManifest.class);

    private byte routingTableSize;
    private RoutingTable routingTable;

    public RegistrySendsNodeManifest(byte routingTableSize, RoutingTable routingTable) {
        this.routingTableSize = routingTableSize;
        this.routingTable = routingTable;
    }

    @Override
    public byte getType() {
        return REGISTRY_SENDS_NODE_MANIFEST;
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
