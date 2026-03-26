import java.util.Random;
import java.util.random.RandomGenerator;

/**
 * A handful of utility functions that both Node and the Coordinator-related classes use.
 */
public class CommonUtil {
    /** Pseudorandom number generator. Used in {@link CommonUtil#randomNap} */
    private static final Random RANDOM = Random.from(RandomGenerator.getDefault());

    /**
     * Waits for a random amount of time
     * @param meanDelay The average amount of time to wait, in milliseconds
     */
    public static void randomNap(int meanDelay) {
        nap((int)RANDOM.nextGaussian(meanDelay, meanDelay * 0.25));
    }

    /**
     * Waits for a set amount of time
     * @param delay The amount of time to wait
     */
    public static void nap(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            // Silently pass.
        }
    }
}
