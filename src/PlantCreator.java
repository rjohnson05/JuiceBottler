/**
 * This class is used for creating, starting, and ending the orange-processing plants. After being started,
 * each of the created plants will run for a designated amount of time before being stopped. Data is displayed upon
 * plant termination, including the total number of oranges provided & processed, the number of bottled produced, and
 * the number of oranges wasted.
 *
 * @author Nate Williams
 */
public class PlantCreator {
    public static final long PROCESSING_TIME = 5 * 1000;

    private static final int NUM_PLANTS = 3;

    public static void main(String[] args) {
        // Startup the plants
        Plant[] plants = new Plant[NUM_PLANTS];
        for (int i = 0; i < NUM_PLANTS; i++) {
            plants[i] = new Plant(i);
            plants[i].startPlant();
        }
        System.out.println("Processing Oranges...\n");

        // Give the plants time to do work
        delay(PROCESSING_TIME, "Plant malfunction");

        // Stop the plant, and wait for it to shut down
        for (Plant p : plants) {
            p.stopPlant();
        }
        for (Plant p : plants) {
            p.waitToStop();
        }

        // Summarize the results
        int totalProvided = 0;
        int totalProcessed = 0;
        int totalBottles = 0;
        int totalWasted = 0;
        for (Plant p : plants) {
            totalProvided += p.getProvidedOranges();
            totalProcessed += p.getProcessedOranges();
            totalBottles += p.getBottles();
            totalWasted += p.getWaste();

            System.out.println("Plant #" + (p.getPlantNum() + 1) + "\n------------");
            System.out.println("Fetched Size: " + p.getFetchedOranges().size());
            System.out.println("Peeled Size: " + p.getPeeledOranges().size());
            System.out.println("Squeezed Size: " + p.getSqueezedOranges().size());
            System.out.println("Bottled Size: " + p.getBottledOranges().size());
            System.out.println("Processed Size: " + p.getProcessedOranges());
            System.out.println(" ");
        }
        System.out.println("Total provided/processed = " + totalProvided + "/" + totalProcessed);
        System.out.println("Created " + totalBottles + " bottles, Wasted " + totalWasted + " oranges");
    }

    /**
     * Waits for a designated number of milliseconds, giving the plants time to run.
     *
     * @param time   long specifying the number of milliseconds the plants should run for
     * @param errMsg String containing the message that should be displayed if the delay is interrupted
     */
    private static void delay(long time, String errMsg) {
        long sleepTime = Math.max(1, time);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            System.err.println(errMsg);
        }
    }
}
