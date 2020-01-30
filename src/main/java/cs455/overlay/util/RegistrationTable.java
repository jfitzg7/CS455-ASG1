package cs455.overlay.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.rmi.runtime.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    public void addNewEntry(LogicalNetworkAddress logicalAddress) {
        if (hasDuplicateEntry(logicalAddress)) {
            LOG.warn("There is already an entry for the address: " + logicalAddress);
        }
        else if (isTableFull()){
            LOG.warn("The registration table is full and no new entries can be added!");
        }
        else {
            int id = 0;
            while ( (registrationTable.putIfAbsent(id, logicalAddress) != null) && (id < 128) ) {
                id++;
            }
            LOG.debug("Added a new entry, the registration table is now: " + registrationTable);
        }
    }

    private boolean isTableFull() {
        // If there is a null value in the table, then that means there is an open ID slot
        return !(registrationTable.containsValue(null));
    }

    private boolean hasDuplicateEntry(LogicalNetworkAddress logicalAddress) {
        return registrationTable.containsValue(logicalAddress);
    }


    public void removeExistingEntry(int id) {
        // Set the corresponding value for the ID back to null
    }

    public boolean containsEntry(LogicalNetworkAddress logicalAddress) {
        return registrationTable.containsValue(logicalAddress);
    }

}
