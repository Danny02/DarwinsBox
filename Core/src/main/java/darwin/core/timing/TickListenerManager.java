/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.core.timing;

/**
 *
 * @author daniel
 */
import java.util.*;

public class TickListenerManager {

    private static final long SECOND_TO_NANO = 1_000_000_000L;
    private final Collection<TickListener> tickListener = new LinkedList<>();
    private final Collection<TickDeltaListener> deltaListener = new LinkedList<>();
    private final long phase;

    public TickListenerManager(int frequency) {
        phase = SECOND_TO_NANO / frequency;
    }

    public boolean isEmpty() {
        return tickListener.isEmpty() && deltaListener.isEmpty();
    }

    public void update(long totalTime, long delta) {
        long ticks = (totalTime / phase) - ((totalTime - delta) / phase);

        if (ticks > 0)
            for (TickListener listener : tickListener) {
                listener.update((int) ticks);
            }

        for (TickDeltaListener listener : deltaListener) {
            listener.update((float) (totalTime % phase) / phase);
        }
    }

    public int getFrequency() {
        return (int) (phase / SECOND_TO_NANO);
    }

    public void addListener(TickListener listener) {
        tickListener.add(listener);
    }

    public boolean removeListener(TickListener listener) {
        return tickListener.remove(listener);
    }

    public void addListener(TickDeltaListener listener) {
        deltaListener.add(listener);
    }

    public boolean removeListener(TickDeltaListener listener) {
        return deltaListener.remove(listener);
    }
}
