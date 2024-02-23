/**
 * Represents a worker at an orange-processing plant. This worker could have one of five different tasks: fetcher, peeler,
 * squeezer, bottler, or processor.
 *
 * @author Ryan Johnson
 */
public class Worker implements Runnable {
    private final Thread thread;
    private final Plant parentPlant;
    private final String job;


    /**
     * Constructor for Worker objects. The worker is assigned a task and a plant and is created on a new thread.
     *
     * @param parentPlant Plant   plant that the worker will work for
     * @param job         String  task that the worker will perform
     */
    public Worker(Plant parentPlant, String job) {
        if (!job.equals("fetch") && !job.equals("peel") && !job.equals("squeeze") && !job.equals("bottle") && !job.equals("process")) {
            throw new ExceptionInInitializerError("Invalid job input");
        }
        thread = new Thread(this, String.format("Plant%s[%s]", parentPlant.getPlantNum(), job));
        this.parentPlant = parentPlant;
        this.job = job;
    }

    /**
     * Run once the thread is started. Moves to a different method depending upon the task the worker is assigned.
     */
    public void run() {
        switch (job) {
            case "fetch" -> runFetcher();
            case "peel" -> runPeeler();
            case "squeeze" -> runSqueezer();
            case "bottle" -> runBottler();
            case "process" -> runProcessor();
        }
    }

    /**
     * Only used by fetcher workers. Fetches a new orange and adds it to its plant's shared list of fetched oranges,
     * before incrementing the shared number of oranges provided to the plant.
     */
    public void runFetcher() {
        while (parentPlant.isTimeToWork()) {
            parentPlant.getFetchedListLock().acquire();
            try {
                BlockingList orangesList = parentPlant.getFetchedOranges();
                orangesList.add(new Orange());
                parentPlant.setFetchedOranges(orangesList);
            } finally {
                parentPlant.getFetchedListLock().release();
            }

            parentPlant.getOrangesProvidedLock().acquire();
            try {
                int providedOranges = parentPlant.getProvidedOranges();
                parentPlant.setProvidedOranges(++providedOranges);
            } finally {
                parentPlant.getOrangesProvidedLock().release();
            }
        }
    }

    /**
     * Only used by peeler workers. Peels the next orange in the shared list of fetched oranges and adds it to its
     * plant's shared list of peeled oranges.
     */
    public void runPeeler() {
        while (parentPlant.isTimeToWork()) {
            parentPlant.getFetchedListLock().acquire();
            try {
                BlockingList fetchedOranges = parentPlant.getFetchedOranges();
                if (fetchedOranges.size() > 0) {
                    Orange o = fetchedOranges.remove();
                    parentPlant.setFetchedOranges(fetchedOranges);

                    o.runProcess();

                    parentPlant.getPeeledListLock().acquire();
                    try {
                        BlockingList peeledOranges = parentPlant.getPeeledOranges();
                        peeledOranges.add(o);
                        parentPlant.setPeeledOranges(peeledOranges);
                    } finally {
                        parentPlant.getPeeledListLock().release();
                    }
                }
            } finally {
                parentPlant.getFetchedListLock().release();
            }
        }
    }

    /**
     * Only used by squeezer workers. Squeezes the next orange in the shared list of peeled oranges and adds it to its
     * plant's shared list of squeezed oranges.
     */
    public void runSqueezer() {
        while (parentPlant.isTimeToWork()) {
            parentPlant.getPeeledListLock().acquire();
            try {
                BlockingList peeledOranges = parentPlant.getPeeledOranges();
                if (peeledOranges.size() > 0) {
                    Orange o = peeledOranges.remove();
                    parentPlant.setPeeledOranges(peeledOranges);

                    o.runProcess();

                    parentPlant.getSqueezedListLock().acquire();
                    try {
                        BlockingList squeezedOranges = parentPlant.getSqueezedOranges();
                        squeezedOranges.add(o);
                        parentPlant.setSqueezedOranges(squeezedOranges);
                    } finally {
                        parentPlant.getSqueezedListLock().release();
                    }
                }
            } finally {
                parentPlant.getPeeledListLock().release();
            }
        }
    }

    /**
     * Only used by bottler workers. Bottles the next orange in the shared list of squeezed oranges and adds it to its
     * plant's shared list of bottled oranges.
     */
    public void runBottler() {
        while (parentPlant.isTimeToWork()) {
            parentPlant.getSqueezedListLock().acquire();
            try {
                BlockingList squeezedOranges = parentPlant.getSqueezedOranges();
                if (squeezedOranges.size() > 0) {
                    Orange o = squeezedOranges.remove();
                    parentPlant.setSqueezedOranges(squeezedOranges);

                    o.runProcess();

                    parentPlant.getBottledListLock().acquire();
                    try {
                        BlockingList bottledOranges = parentPlant.getBottledOranges();
                        bottledOranges.add(o);
                        parentPlant.setBottledOranges(bottledOranges);
                    } finally {
                        parentPlant.getBottledListLock().release();
                    }
                }
            } finally {
                parentPlant.getSqueezedListLock().release();
            }
        }
    }

    /**
     * Only used by processor workers. Processes the next orange in the shared list of bottled oranges and increments
     * the shared number of processed oranges.
     */
    public void runProcessor() {
        while (parentPlant.isTimeToWork()) {
            parentPlant.getBottledListLock().acquire();
            try {
                BlockingList bottledOranges = parentPlant.getBottledOranges();
                if (bottledOranges.size() > 0) {
                    Orange o = bottledOranges.remove();
                    parentPlant.setBottledOranges(bottledOranges);

                    o.runProcess();

                    parentPlant.getOrangesProcessedLock().acquire();
                    try {
                        int orangesProcessed = parentPlant.getProcessedOranges();
                        orangesProcessed++;
                        parentPlant.setProcessedOranges(orangesProcessed);
                    } finally {
                        parentPlant.getOrangesProcessedLock().release();
                    }
                }
            } finally {
                parentPlant.getBottledListLock().release();
            }
        }
    }

    /**
     * Returns the thread being used to run the worker tasks.
     *
     * @return Thread  thread used to run the worker's task
     */
    public Thread getThread() {
        return this.thread;
    }
}
