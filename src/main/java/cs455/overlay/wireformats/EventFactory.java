package cs455.overlay.wireformats;

import cs455.overlay.util.RoutingEntry;
import cs455.overlay.util.RoutingTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class EventFactory implements Protocol {

    private static Logger LOG = LogManager.getLogger(EventFactory.class);

    public Event factoryMethod(byte[] data) {
        try {
            ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
            DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
            byte type = din.readByte();
            if (type == OVERLAY_NODE_SENDS_REGISTRATION) {
                LOG.info("Constructing new OVERLAY_NODE_SENDS_REGISTRATION event");
                byte addressLength = din.readByte();
                byte[] address = new byte[addressLength];
                din.readFully(address);
                int portNumber = din.readInt();
                return new OverlayNodeSendsRegistration(address, addressLength, portNumber);
            }
            else if (type == REGISTRY_REPORTS_REGISTRATION_STATUS) {
                LOG.info("Constructing new REGISTRY_REPORTS_REGISTRATION_STATUS event");
                int successStatus = din.readInt();
                byte informationStringLength = din.readByte();
                byte[] informationString = new byte[informationStringLength];
                din.readFully(informationString);
                return new RegistryReportsRegistrationStatus(successStatus, informationString);
            }
            else if (type == OVERLAY_NODE_SENDS_DEREGISTRATION) {
                LOG.info("Constructing new OVERLAY_NODE_SENDS_DEREGISTRATION event");
                byte addressLength = din.readByte();
                byte[] address = new byte[addressLength];
                din.readFully(address);
                int portNumber = din.readInt();
                int assignedNodeID = din.readInt();
                return new OverlayNodeSendsDeregistration(addressLength, address, portNumber, assignedNodeID);
            }
            else if (type == REGISTRY_REPORTS_DEREGISTRATION_STATUS) {
                LOG.info("Constructing new REGISTRY_REPORTS_DEREGISTRATION_STATUS event");
                int successStatus = din.readInt();
                byte informationStringLength = din.readByte();
                byte[] informationString = new byte[informationStringLength];
                din.readFully(informationString);
                return new RegistryReportsDeregistrationStatus(successStatus, informationString);
            }
            else if (type == REGISTRY_SENDS_NODE_MANIFEST) {
                LOG.info("Constructing new REGISTRY_SENDS_NODE_MANIFEST message");
                byte routingTableSize = din.readByte();
                RoutingTable routingTable = new RoutingTable();
                for (int i = 0; i < routingTableSize; i++) {
                    int hopsAway = (int) Math.pow(2, i);
                    int nodeID = din.readInt();
                    byte IPAddressLength = din.readByte();
                    byte[] IPAddress = new byte[IPAddressLength];
                    din.readFully(IPAddress);
                    int portNumber = din.readInt();
                    RoutingEntry entry = new RoutingEntry(IPAddress, portNumber, nodeID, hopsAway);
                    routingTable.addRoutingEntry(entry);
                }
                byte nodeIDListSize = din.readByte();
                int[] nodeIDList = new int[nodeIDListSize];
                for (int i = 0; i < nodeIDListSize; i++) {
                    nodeIDList[i] = din.readInt();
                }
                return new RegistrySendsNodeManifest(routingTableSize, routingTable, nodeIDList);
            }
            else if (type == NODE_REPORTS_OVERLAY_SETUP_STATUS) {
                LOG.info("Constructing new NODE_REPORTS_OVERLAY_SETUP_STATUS event");
                int successStatus = din.readInt();
                byte informationStringLength = din.readByte();
                byte[] informationString = new byte[informationStringLength];
                din.readFully(informationString);
                return new NodeReportsOverlaySetupStatus(successStatus, informationString);
            }
            else if (type == REGISTRY_REQUESTS_TASK_INITIATE) {
                LOG.info("Constructing new REGISTRY_REQUESTS_TASK_INITIATE event");
                int numberOfMessages = din.readInt();
                return new RegistryRequestsTaskInitiate(numberOfMessages);
            }
            else if (type == OVERLAY_NODE_SENDS_DATA){
                LOG.info("Constructing new OVERLAY_NODE_SENDS_DATA event");
                int destinationID = din.readInt();
                int sourceID = din.readInt();
                int payload = din.readInt();
                int disseminationTraceLength = din.readInt();
                int[] disseminationTrace = new int[disseminationTraceLength];
                for (int i=0; i < disseminationTraceLength; i++) {
                    disseminationTrace[i] = din.readInt();
                }
                return new OverlayNodeSendsData(destinationID, sourceID, payload, disseminationTrace);
            }
            else if (type == OVERLAY_NODE_REPORTS_TASK_FINISHED) {
                LOG.info("Constructing new OVERLAY_NODE_REPORTS_TASK_FINISHED event");
                byte IPAddressLength = din.readByte();
                byte[] IPAddress = new byte[IPAddressLength];
                din.readFully(IPAddress);
                int portNumber = din.readInt();
                int nodeID = din.readInt();
                return new OverlayNodeReportsTaskFinished(IPAddress, portNumber, nodeID);
            }
            else if (type == REGISTRY_REQUESTS_TRAFFIC_SUMMARY) {
                LOG.info("Constructing new REGISTRY_REQUESTS_TRAFFIC_SUMMARY event");
                return new RegistryRequestsTrafficSummary();
            }
            else if (type == OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY) {
                LOG.info("Constructing new OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY event");
                int assignedNodeID = din.readInt();
                int sentPackets = din.readInt();
                int relayedPackets = din.readInt();
                long sendSummation = din.readLong();
                int receivedPackets = din.readInt();
                long receiveSummation = din.readLong();
                return new OverlayNodeReportsTrafficSummary(assignedNodeID, sentPackets, relayedPackets, receivedPackets, sendSummation, receiveSummation);
            }
            else {
                LOG.warn("Unknown message type received: " + type);
            }
            baInputStream.close();
            din.close();
        } catch (IOException ioe) {
            LOG.error("An exception occurred while trying to unmarshal the data", ioe);
        }
        //return null if no valid message types are detected.
        return null;
    }
}
