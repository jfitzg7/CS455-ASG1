package cs455.overlay.util;

/* The purpose of this class is to use a thread-safe list
 * in order to randomly assign IDs (0-127) to MessagingNodes
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class RandomIDGenerator {

    private Logger LOG = LogManager.getLogger(RandomIDGenerator.class);

    private List<Integer> availableIDList;

    public RandomIDGenerator() {
        availableIDList = Collections.synchronizedList(new ArrayList<>(128));
        for (int i=0; i < 127; i++) {
            availableIDList.add(i);
        }
        LOG.debug("Constructed new ID list: " + Arrays.toString(availableIDList.toArray()));
    }

    public List<Integer> getAvailableIDList() {
        return availableIDList;
    }

    public int[] getCurrentPrimitiveAvailableIDList() {
        int[] currentIDList = new int[availableIDList.size()];
        for (int i=0; i < currentIDList.length; i++)
        {
            currentIDList[i] = availableIDList.get(i);
        }
        return currentIDList;
    }

    public int getRandomID() {
        int id = -1;
        Random rand = new Random();
        if (this.availableIDList.size() > 0){
            int index = rand.nextInt(availableIDList.size());
            id = availableIDList.remove(index);
        } else {
            LOG.warn("All valid IDs have already been registered");
        }
        return id;
    }

    public void replaceID() {

    }
}
