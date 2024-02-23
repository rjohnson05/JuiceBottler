/**
 * Lock to be used for shared data types. Only threads having acquired the lock may access locked data types.
 *
 * @author Ryan Johnson
 */
public class Mutex {
    private boolean locked = false;

    /**
     * Signals a desire to obtain possession of the lock. Access to the lock is blocked until the lock is available.
     */
    public synchronized void acquire() {
        while (locked) {
            try {
                wait();
            } catch (Exception e) {
                throw new IllegalStateException("Trying to wait");
            }
        }
        locked = true;
    }

    /**
     * Releases possession of the lock after completing a task.
     */
    public synchronized void release() {
        if (!locked) {
            throw new IllegalStateException("Attempting to release a non-acquired lock");
        }
        locked = false;
        notifyAll();
    }
}