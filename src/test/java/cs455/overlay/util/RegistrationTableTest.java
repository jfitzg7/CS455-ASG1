package cs455.overlay.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sun.rmi.runtime.Log;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationTableTest {

    private static RegistrationTable registrationTable;

    @BeforeAll
    public static void initialize() {
        registrationTable = new RegistrationTable();
    }

    @Test
    public void addNewEntryTest() {
        LogicalNetworkAddress logicalAddress1 = new LogicalNetworkAddress(new byte[]{100, 100, 100, 100}, 5000);
        LogicalNetworkAddress logicalAddress2 = new LogicalNetworkAddress(new byte[]{100, 100, 100, 100}, 5000);
        registrationTable.addNewEntry(logicalAddress1);
        assertTrue(registrationTable.containsEntry(logicalAddress2));
    }
}