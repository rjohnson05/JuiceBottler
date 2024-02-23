import java.util.Iterator;
import java.util.LinkedList;

/**
 * List meant to be shared between several threads, allowing only one thread to make changes at a time.
 *
 * @author Ryan Johnson
 */
public class BlockingList {
    private final LinkedList<Orange> orangeList = new LinkedList<>();
    private final Mutex lock = new Mutex();

    public BlockingList() {
    }

    /**
     * Adds an orange to the list. Only one thread may access this method at a time.
     *
     * @param orange Orange orange to be added to the list
     */
    public synchronized void add(Orange orange) {
        lock.acquire();
        try {
            orangeList.add(orange);
        } finally {
            lock.release();
        }
    }

    /**
     * Removes and returns the first orange in the list.
     *
     * @return Orange  first orange in the list, which has now been removed
     */
    public synchronized Orange remove() {
        lock.acquire();
        Orange firstOrange;
        try {
            firstOrange = orangeList.remove();
        } finally {
            lock.release();
        }
        return firstOrange;
    }

    /**
     * Returns the number of oranges in the list.
     *
     * @return int  number of oranges in the list
     */
    public int size() {
        return orangeList.size();
    }

    /**
     * Displays the list of oranges in a String format.
     *
     * @return String list of oranges in String format
     */
    public String toString() {
        Iterator<Orange> it = orangeList.iterator();
        StringBuilder retStr = new StringBuilder();
        retStr.append("[");
        while (it.hasNext()) {
            retStr.append(it.next()).append(", ");
        }
        retStr.append("]");
        return retStr.toString();
    }
}
