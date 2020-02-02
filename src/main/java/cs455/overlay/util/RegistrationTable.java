package cs455.overlay.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegistrationTable {

    private Logger LOG = LogManager.getLogger(RegistrationTable.class);

    private Map<Integer, MessagingNodeInfo> registrationTable;

    public RegistrationTable() {
        registrationTable = Collections.synchronizedMap(new HashMap<>());
        for (int i=0; i < 128; i++) {
            registrationTable.put(i, null);
        }
        LOG.debug("The registration table after construction: " + registrationTable);
    }

    public synchronized boolean addNewEntry(MessagingNodeInfo info) {
        if (containsEntry(info)) {
            LOG.warn("There is already an entry for the address: " + info);
            return false;
        }
        else if (isTableFull()){
            LOG.warn("The registration table is full and no new entries can be added!");
            return false;
        }
        else {
            int id = 0;
            while ( (registrationTable.putIfAbsent(id, info) != null) && (id < 128) ) {
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

    public synchronized boolean removeExistingEntry(int id, MessagingNodeInfo info) {
        if (!containsEntry(info)) {
            LOG.warn("There is currently no entry in the registration table for " + info);
            return false;
        }
        if (registrationTable.replace(id, info, null)) {
            LOG.debug("Removing MessagingNode with id: " + id + " and address: " + info);
            return true;
        } else {
            LOG.warn("The address held at the specified ID does not match the address of the MessagingNode that wants to deregister");
            return false;
        }
    }

    public synchronized boolean containsEntry(MessagingNodeInfo info) {
        return registrationTable.containsValue(info);
    }

    public synchronized int getID(MessagingNodeInfo info) {
        for (Map.Entry<Integer, MessagingNodeInfo> entry : registrationTable.entrySet()) {
            if (Objects.equals(info, entry.getValue())) {
                return entry.getKey();
            }
        }
        LOG.warn("There is no ID assigned to the address: " + info);
        return -1;
    }

    public synchronized int countEntries() {
        int numberOfEntries = 0;
        for (Map.Entry<Integer, MessagingNodeInfo> entry : registrationTable.entrySet()) {
            if (Objects.equals(null, entry.getValue())); //Do nothing
            else {
                numberOfEntries++;
            }
        }
        return numberOfEntries;
    }

}
