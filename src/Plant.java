/**
 * Represents an orange-processing plant. A plant contains several workers, which accomplish different tasks necessary
 * for the processing of an orange. There are 5 different kinds of worker: fetchers, peelers, squeezers, bottlers, and
 * processors. At least one of every type of worker needs to be created to process any oranges. Upon termination, the plant
 * waits for the worker threads to finish before shutting down.
 *
 * @author Ryan Johnson
 */
public class Plant {
    private final int plantNum;
    private final int ORANGES_PER_BOTTLE = 3;
    private final int NUM_FETCHERS = 1;
    private final int NUM_PEELERS = 3;
    private final int NUM_SQUEEZERS = 3;
    private final int NUM_BOTTLERS = 2;
    private final int NUM_PROCESSORS = 1;

    private final Worker[] fetchers = new Worker[NUM_FETCHERS];
    private final Worker[] peelers = new Worker[NUM_PEELERS];
    private final Worker[] squeezers = new Worker[NUM_SQUEEZERS];
    private final Worker[] bottlers = new Worker[NUM_BOTTLERS];
    private final Worker[] processors = new Worker[NUM_PROCESSORS];

    private volatile BlockingList fetchedOranges = new BlockingList();
    private volatile BlockingList peeledOranges = new BlockingList();
    private volatile BlockingList squeezedOranges = new BlockingList();
    private volatile BlockingList bottledOranges = new BlockingList();

    private final Mutex fetchedListLock = new Mutex();
    private final Mutex peeledListLock = new Mutex();
    private final Mutex squeezedListLock = new Mutex();
    private final Mutex bottledListLock = new Mutex();
    private final Mutex orangesProvidedLock = new Mutex();
    private final Mutex orangesProcessedLock = new Mutex();

    private int orangesProvided;
    private int orangesProcessed;
    private volatile boolean timeToWork;

    /**
     * Constructor for Plant objects. Creates each of the worker threads, which will be started at a later point.
     *
     * @param threadNum int  the ID number assigned to the plant
     */
    Plant(int threadNum) {
        orangesProvided = 0;
        orangesProcessed = 0;

        this.plantNum = threadNum;

        // Create a separate thread for each individual worker in a plant
        for (int i = 0; i < fetchers.length; i++) {
            fetchers[i] = new Worker(this, "fetch");
        }
        for (int i = 0; i < peelers.length; i++) {
            peelers[i] = new Worker(this, "peel");
        }
        for (int i = 0; i < squeezers.length; i++) {
            squeezers[i] = new Worker(this, "squeeze");
        }
        for (int i = 0; i < bottlers.length; i++) {
            bottlers[i] = new Worker(this, "bottle");
        }
        for (int i = 0; i < processors.length; i++) {
            processors[i] = new Worker(this, "process");
        }
    }

    /**
     * Starts each of the worker threads to begin processing oranges.
     */
    public void startPlant() {
        timeToWork = true;

        // Start each of the worker threads
        for (Worker fetcher : fetchers) {
            fetcher.getThread().start();
        }
        for (Worker peeler : peelers) {
            peeler.getThread().start();
        }
        for (Worker squeezer : squeezers) {
            squeezer.getThread().start();
        }
        for (Worker bottler : bottlers) {
            bottler.getThread().start();
        }
        for (Worker processor : processors) {
            processor.getThread().start();
        }
    }

    /**
     * Sets the timeToWork boolean to false, effectively stopping the worker threads.
     */
    public void stopPlant() {
        timeToWork = false;
    }

    /**
     * Waits for the worker threads to stop and rejoin to the main thread before shutting down the plant.
     */
    public void waitToStop() {
        try {
            for (Worker worker : fetchers) {
                worker.getThread().join();
            }
            for (Worker worker : peelers) {
                worker.getThread().join();
            }
            for (Worker worker : squeezers) {
                worker.getThread().join();
            }
            for (Worker worker : bottlers) {
                worker.getThread().join();
            }
            for (Worker worker : processors) {
                worker.getThread().join();
            }
        } catch (InterruptedException e) {
            System.err.println("Worker thread stop malfunction");
        }
    }

    /**
     * Returns the number of oranges that were brought into the plant for processing.
     *
     * @return int number of oranges brought into the plant for processing
     */
    public int getProvidedOranges() {
        return orangesProvided;
    }

    /**
     * Sets the number of oranges that were brought into the plant for processing.
     *
     * @param orangesProvided int number of oranges brought into the plant for processing
     */
    public void setProvidedOranges(int orangesProvided) {
        this.orangesProvided = orangesProvided;
    }

    /**
     * Returns the number of oranges that have been processed by the plant.
     *
     * @return int number of oranges that the plant has processed
     */
    public int getProcessedOranges() {
        return orangesProcessed;
    }

    /**
     * Sets the number of oranges that have been processed by the plant.
     *
     * @param orangesProcessed int number of oranges the plant has processed
     */
    public void setProcessedOranges(int orangesProcessed) {
        this.orangesProcessed = orangesProcessed;
    }

    /**
     * Returns the number of bottles the plant has produced. Several oranges are required to fill a single bottle.
     *
     * @return int number of bottles the plant has produced
     */
    public int getBottles() {
        return orangesProcessed / ORANGES_PER_BOTTLE;
    }

    /**
     * Returns the number of oranges that were brought into the plant, but were not fully processed before the plant was
     * shut down.
     *
     * @return int number of wasted oranges
     */
    public int getWaste() {
        return (orangesProcessed % ORANGES_PER_BOTTLE) + (orangesProvided - orangesProcessed);
    }

    /**
     * Returns the list of oranges that have been brought into the plant, but have not yet been peeled.
     *
     * @return BlockingList list of oranges that have been brought into the plant, but have not yet been peeled
     */
    public BlockingList getFetchedOranges() {
        return fetchedOranges;
    }

    /**
     * Sets the list of oranges that have been brought into the plant, but have not yet been peeled.
     *
     * @param fetchedOranges BlockingList list of oranges that have been brought into the plant, but have not yet been peeled
     */
    public void setFetchedOranges(BlockingList fetchedOranges) {
        this.fetchedOranges = fetchedOranges;
    }

    /**
     * Returns the list of oranges that have been fetched and peeled, but have not yet been squeezed.
     *
     * @return BlockingList list of oranges that have been fetched and peeled, but have not yet been squeezed
     */
    public BlockingList getPeeledOranges() {
        return peeledOranges;
    }

    /**
     * Sets the list of oranges that have been fetched and peeled, but have not yet been squeezed.
     *
     * @param peeledOranges BlockingList list of oranges that have been fetched and peeled, but have not yet been squeezed
     */
    public void setPeeledOranges(BlockingList peeledOranges) {
        this.peeledOranges = peeledOranges;
    }

    /**
     * Returns the list of oranges that have been squeezed, but have not yet been bottled.
     *
     * @return BlockingList list of oranges that have been squeezed, but have not yet been bottled
     */
    public BlockingList getSqueezedOranges() {
        return squeezedOranges;
    }

    /**
     * Sets the list of oranges that have been squeezed, but have not yet been bottled.
     *
     * @param squeezedOranges BlockingList list of oranges that have been squeezed, but have not yet been bottled
     */
    public void setSqueezedOranges(BlockingList squeezedOranges) {
        this.squeezedOranges = squeezedOranges;
    }

    /**
     * Returns the list of oranges that have been bottled, but have not yet been processed.
     *
     * @return BlockingList list of oranges that have been bottled, but have not yet been processed
     */
    public BlockingList getBottledOranges() {
        return bottledOranges;
    }

    /**
     * Sets the list of oranges that have been bottled, but have not yet been processed.
     *
     * @param bottledOranges BlockingList list of oranges that have been bottled, but have not yet been processed
     */
    public void setBottledOranges(BlockingList bottledOranges) {
        this.bottledOranges = bottledOranges;
    }

    /**
     * Returns whether the workers should continue processing oranges.
     *
     * @return boolean whether the workers should continue working (true if the workers should continue working; false otherwise)
     */
    public boolean isTimeToWork() {
        return timeToWork;
    }

    /**
     * Returns the ID number for the plant.
     *
     * @return int ID number of the plant
     */
    public int getPlantNum() {
        return plantNum;
    }

    /**
     * Returns the lock for the shared list of fetched oranges.
     *
     * @return Mutex lock for the shared list of fetched oranges
     */
    public Mutex getFetchedListLock() {
        return fetchedListLock;
    }

    /**
     * Returns the lock for the shared list of peeled oranges.
     *
     * @return Mutex lock for the shared list of peeled oranges
     */
    public Mutex getPeeledListLock() {
        return peeledListLock;
    }

    /**
     * Returns the lock for the shared list of squeezed oranges.
     *
     * @return Mutex lock for the shared list of squeezed oranges
     */
    public Mutex getSqueezedListLock() {
        return squeezedListLock;
    }

    /**
     * Returns the lock for the shared list of bottled oranges.
     *
     * @return Mutex lock for the shared list of bottled oranges
     */
    public Mutex getBottledListLock() {
        return bottledListLock;
    }

    /**
     * Returns the lock for the shared integer stating the number of oranges provided to the plant.
     *
     * @return Mutex lock for the shared integer stating the number of oranges provided to the plant
     */
    public Mutex getOrangesProvidedLock() {
        return orangesProvidedLock;
    }

    /**
     * Returns the lock for the shared integer stating the number of oranges processed by the plant.
     *
     * @return Mutex lock for the shared integer stating the number of oranges processed by the plant
     */
    public Mutex getOrangesProcessedLock() {
        return orangesProcessedLock;
    }
}