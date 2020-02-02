package cs455.overlay.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationTableTest {

    private RegistrationTable registrationTable;
    private MessagingNodeInfo messagingNodeInfo;

    @BeforeEach
    public void initialize() {
        registrationTable = new RegistrationTable();
        LogicalNetworkAddress logicalAddress = new LogicalNetworkAddress(new byte[]{100, 100, 100, 100}, 5000);
        messagingNodeInfo = new MessagingNodeInfo(logicalAddress, new Socket());
    }

    @Test
    public void addNewEntryTest() {
        LogicalNetworkAddress logicalAddress = new LogicalNetworkAddress(new byte[]{100, 100, 100, 100}, 5000);
        MessagingNodeInfo messagingNodeInfo2 = new MessagingNodeInfo(logicalAddress, new Socket());
        assertTrue(registrationTable.addNewEntry(messagingNodeInfo));

        //quick assertion for equals()/hashCode()
        assertTrue(registrationTable.containsEntry(messagingNodeInfo2));
    }

    @Test
    public void duplicateEntryTest() {
        LogicalNetworkAddress logicalAddress = new LogicalNetworkAddress(new byte[]{100, 100, 100, 100}, 5000);
        MessagingNodeInfo messagingNodeInfo2 = new MessagingNodeInfo(logicalAddress, new Socket());

        registrationTable.addNewEntry(messagingNodeInfo);
        assertFalse(registrationTable.addNewEntry(messagingNodeInfo));

        //Ensure that it works with a different object (equals()/hashCode() verification)
        assertFalse(registrationTable.addNewEntry(messagingNodeInfo2));
    }

    @Test
    public void fullTableTest() {
        // TODO
    }

    @Test
    public void getIDTest() {
        registrationTable.addNewEntry(messagingNodeInfo);
        int id = registrationTable.getID(messagingNodeInfo);
        assertTrue(id >= 0 && id <= 127);
    }

    @Test
    public void countEntriesEmptyTest() {
        assertEquals(0, registrationTable.countEntries());
    }

    @Test
    public void count5EntriesTest() {
        for (int i=0; i < 5; i++) {
            LogicalNetworkAddress logicalAddress = new LogicalNetworkAddress(new byte[]{10, 10, 10, 10}, i + 5000);
            registrationTable.addNewEntry(new MessagingNodeInfo(logicalAddress, new Socket()));
        }
        assertEquals(5, registrationTable.countEntries());
    }

    @Test
    public void getInvalidIDTest() {
        int id = registrationTable.getID(messagingNodeInfo);
        assertEquals(-1, id);
    }

    @Test
    public void removeExistingEntryTest() {
        registrationTable.addNewEntry(messagingNodeInfo);
        int id = registrationTable.getID(messagingNodeInfo);
        assertTrue(registrationTable.removeExistingEntry(id, messagingNodeInfo));
    }

    @Test
    public void removeNonExistingEntryTest() {
        assertFalse(registrationTable.removeExistingEntry(50, messagingNodeInfo));
    }

    @Test
    public void removeMismatchedEntryTest() {
        LogicalNetworkAddress mismatchedAddress = new LogicalNetworkAddress(new byte[]{100, 100, 100, 100}, 50001);
        MessagingNodeInfo mismatchedInfo = new MessagingNodeInfo(mismatchedAddress, new Socket());
        registrationTable.addNewEntry(messagingNodeInfo);
        int id = registrationTable.getID(messagingNodeInfo);
        assertFalse(registrationTable.removeExistingEntry(id, mismatchedInfo));
    }

}