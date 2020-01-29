package cs455.overlay.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomIDGeneratorTest {

    private RandomIDGenerator idGenerator;

    @BeforeEach
    public void initialize() {
        idGenerator = new RandomIDGenerator();
    }

    @Test
    public void initialIDListTest() {

    }

    @Test
    public void getRandomIDTest() {
        int randomID = idGenerator.getRandomID();
        assertTrue(randomID >= 0 && randomID <= 127);
        assertFalse(idGenerator.getAvailableIDList().contains(randomID));
    }


}