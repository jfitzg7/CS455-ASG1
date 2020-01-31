package cs455.overlay.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationTableTest {

    private RegistrationTable registrationTable;
    private LogicalNetworkAddress logicalAddress;

    @BeforeEach
    public void initialize() {
        registrationTable = new RegistrationTable();
        logicalAddress = new LogicalNetworkAddress(new byte[]{100, 100, 100, 100}, 5000);
    }

    @Test
    public void addNewEntryTest() {
        LogicalNetworkAddress logicalAddress2 = new LogicalNetworkAddress(new byte[]{100, 100, 100, 100}, 5000);
        assertTrue(registrationTable.addNewEntry(logicalAddress));

        //quick assertion for equals()/hashCode()
        assertTrue(registrationTable.containsEntry(logicalAddress2));
    }

    @Test
    public void duplicateEntryTest() {
        LogicalNetworkAddress logicalAddress2 = new LogicalNetworkAddress(new byte[]{100, 100, 100, 100}, 5000);
        registrationTable.addNewEntry(logicalAddress);
        assertFalse(registrationTable.addNewEntry(logicalAddress));

        //Ensure that it works with a different object (equals()/hashCode() verification)
        assertFalse(registrationTable.addNewEntry(logicalAddress));
    }

    @Test
    public void fullTableTest() {
        // TODO
    }

    @Test
    public void getIDTest() {
        registrationTable.addNewEntry(logicalAddress);
        int id = registrationTable.getID(logicalAddress);
        assertTrue(id >= 0 && id <= 127);
    }

    @Test
    public void getInvalidIDTest() {
        int id = registrationTable.getID(logicalAddress);
        assertEquals(-1, id);
    }

    @Test
    public void removeExistingEntryTest() {
        registrationTable.addNewEntry(logicalAddress);
        int id = registrationTable.getID(logicalAddress);
        assertTrue(registrationTable.removeExistingEntry(id, logicalAddress));
    }

    @Test
    public void removeNonExistingEntryTest() {
        assertFalse(registrationTable.removeExistingEntry(50, logicalAddress));
    }

    @Test
    public void removeMismatchedEntryTest() {
        LogicalNetworkAddress mismatchedAddress = new LogicalNetworkAddress(new byte[]{100, 100, 100, 100}, 50001);
        registrationTable.addNewEntry(logicalAddress);
        int id = registrationTable.getID(logicalAddress);
        assertFalse(registrationTable.removeExistingEntry(id, mismatchedAddress));
    }

}