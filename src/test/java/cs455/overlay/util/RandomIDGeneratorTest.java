package cs455.overlay.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomIDGeneratorTest {

    private static RandomIDGenerator idGenerator;

    @BeforeAll
    public static void initialize() {
        idGenerator = new RandomIDGenerator();
    }

    @Test
    public void getRandomIDAndReplaceIDTest() {
        int randomID = idGenerator.getRandomID();
        assertTrue(randomID >= 0 && randomID <= 127);
        assertFalse(idGenerator.getAvailableIDList().contains(randomID));
        idGenerator.replaceID(randomID);
        assertTrue(idGenerator.getAvailableIDList().contains(randomID));
        assertFalse(idGenerator.replaceID(randomID));
    }

    @Test
    public void getRandomIDExhaustiveTest() {

    }
}
