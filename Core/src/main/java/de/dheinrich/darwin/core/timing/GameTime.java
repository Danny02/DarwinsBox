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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class GameTime {

    private static class Start {

        private static final long time = System.nanoTime();
    }
    private static final long SECOND_TO_NANO = 1_000_000_000L;
    private static final double NANO_TO_SECOND = 1. / SECOND_TO_NANO;

    public static final GameTime INSTANCE = new GameTime();

    private long elapsed = 0;

    private Map<Integer, TickListenerManager> tickListener = new HashMap<>();
    private Collection<GameTimeListener> timeListener = new LinkedList<>();

    private GameTime() {
    }

    public void update() {
        long start = Start.time;

        long now = System.nanoTime();

        long delta = now - start - elapsed;

        updatedTimeListener(delta);
        updatedTickListener(delta);

        elapsed += delta;
    }

    private void updatedTimeListener(long nanoSeconds) {
        double delta = nanoSeconds * NANO_TO_SECOND;
        for (GameTimeListener listener : timeListener) {
            listener.update(delta);
        }
    }

    private void updatedTickListener(long nanoSeconds) {
        for (TickListenerManager lm : tickListener.values()) {
            lm.update(elapsed, nanoSeconds);
        }
    }

    /**
     *
     * @param frequency of ticks in herz
     * @return
     */
    public void addTickListener(int frequency, GameTickListener listener) {
        TickListenerManager tlm = tickListener.get(frequency);
        if(tlm == null)
        {
            tlm = new TickListenerManager(frequency);
            tickListener.put(frequency, tlm);
        }
        tlm.addListener(listener);
    }

    public void removeTickListener(GameTickListener listener) {
         for (TickListenerManager lm : tickListener.values()) {
            if(lm.removeListener(listener))
            {
                tickListener.remove(lm.getFrequency());
                break;
            }
        }
    }

    public void addTimeListener(GameTimeListener listener) {
        timeListener.add(listener);
    }

    public void removeTimeistener(GameTimeListener listener) {
        timeListener.remove(listener);
    }

    public double getGameTime() {
        return elapsed * NANO_TO_SECOND;
    }

    /**
     *
     * @param frequency of ticks in herz
     * @return
     */
    public long getGameTicks(int frequency) {
        return elapsed / (frequency * SECOND_TO_NANO);
    }
}
