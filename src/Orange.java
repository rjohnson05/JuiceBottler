/**
 * Represents an orange to be processed by a plant. An orange can be in one of five different states: fetched, peeled,
 * squeezed, bottled, or processed. Before moving to the next state, a set amount of time must pass to simulate the work
 * of the state being accomplished.
 *
 * @author Nate Williams
 */
public class Orange {
    public enum State {
        Fetched(15), // 15
        Peeled(38),  // 38
        Squeezed(29),
        Bottled(17),
        Processed(1);

        private static final int finalIndex = State.values().length - 1;

        final int timeToComplete;

        State(int timeToComplete) {
            this.timeToComplete = timeToComplete;
        }

        State getNext() {
            int currIndex = this.ordinal();
            if (currIndex >= finalIndex) {
                throw new IllegalStateException("Already at final state");
            }
            return State.values()[currIndex + 1];
        }
    }

    private State state;

    /**
     * Constructor for an Orange object. Assigns the state to 'Fetched'.
     */
    public Orange() {
        state = State.Fetched;
        doWork();
    }

    /**
     * Returns the current state of the orange.
     *
     * @return State current state of the orange
     */
    public State getState() {
        return state;
    }

    /**
     * Moves the orange to the next state.
     */
    public void runProcess() {
        // Don't attempt to process an already completed orange
        if (state == State.Processed) {
            throw new IllegalStateException("This orange has already been processed");
        }
        doWork();
        state = state.getNext();
    }

    /**
     * Puts the thread to sleep for the time it takes to complete the current task.
     */
    private void doWork() {
        // Sleep for the amount of time necessary to do the work
        try {
            Thread.sleep(state.timeToComplete);
        } catch (InterruptedException e) {
            System.err.println("Incomplete orange processing, juice may be bad");
        }
    }
}
