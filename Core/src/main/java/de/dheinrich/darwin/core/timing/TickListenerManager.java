/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.darwin.core.timing;

/**
 *
 * @author daniel
 */
import java.util.Collection;
import java.util.LinkedList;

public class TickListenerManager {

    private static final long SECOND_TO_NANO = 1_000_000_000L;
    private final Collection<GameTickListener> tickListener = new LinkedList<>();
    private final long frequency;

    public TickListenerManager(int frequency) {
        this.frequency = frequency * SECOND_TO_NANO;
    }

    public void addListener(GameTickListener listener) {
        tickListener.add(listener);
    }

    public boolean removeListener(GameTickListener listener) {
        tickListener.remove(listener);
        return tickListener.isEmpty();
    }

    public void update(long totalTime, long delta) {
        long ticks = (totalTime / frequency) - ((totalTime - delta) / frequency);
        for (GameTickListener listener : tickListener) {
            listener.update((int) ticks);
        }
    }

    public int getFrequency() {
        return (int) (frequency / SECOND_TO_NANO);
    }
}
