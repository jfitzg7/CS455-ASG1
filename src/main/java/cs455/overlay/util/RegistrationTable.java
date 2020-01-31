package cs455.overlay.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegistrationTable {

    private Logger LOG = LogManager.getLogger(RegistrationTable.class);

    private Map<Integer, LogicalNetworkAddress> registrationTable;

    public RegistrationTable() {
        registrationTable = Collections.synchronizedMap(new HashMap<>());
        for (int i=0; i < 128; i++) {
            registrationTable.put(i, null);
        }
        LOG.debug("The registration table after construction: " + registrationTable);
    }

    // TODO create a method for counting the number of nodes currently registered

    public synchronized boolean addNewEntry(LogicalNetworkAddress logicalAddress) {
        if (containsEntry(logicalAddress)) {
            LOG.warn("There is already an entry for the address: " + logicalAddress);
            return false;
        }
        else if (isTableFull()){
            LOG.warn("The registration table is full and no new entries can be added!");
            return false;
        }
        else {
            int id = 0;
            while ( (registrationTable.putIfAbsent(id, logicalAddress) != null) && (id < 128) ) {
                id++;
            }
            LOG.debug("Added a new entry, the registration table is now: " + registrationTable);
            return true;
        }
    }

    private boolean isTableFull() {
        // If there is a null value in the table, then that means there is an open ID slot
        return !(registrationTable.containsValue(null));
    }

    public synchronized boolean removeExistingEntry(int id, LogicalNetworkAddress logicalAddress) {
        if (!containsEntry(logicalAddress)) {
            LOG.warn("There is currently no entry in the registration table for " + logicalAddress);
            return false;
        }
        if (registrationTable.replace(id, logicalAddress, null)) {
            LOG.debug("Removing MessagingNode with id: " + id + " and address: " + logicalAddress);
            return true;
        } else {
            LOG.warn("The address held at the specified ID does not match the address of the MessagingNode that wants to deregister");
            return false;
        }
    }

    public synchronized boolean containsEntry(LogicalNetworkAddress logicalAddress) {
        return registrationTable.containsValue(logicalAddress);
    }

    public synchronized int getID(LogicalNetworkAddress logicalNetworkAddress) {
        for (Map.Entry<Integer, LogicalNetworkAddress> entry : registrationTable.entrySet()) {
            if (Objects.equals(logicalNetworkAddress, entry.getValue())) {
                return entry.getKey();
            }
        }
        LOG.warn("There is no ID assigned to the address: " + logicalNetworkAddress);
        return -1;
    }

}
