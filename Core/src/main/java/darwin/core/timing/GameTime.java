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

public class GameTime {

    private static class Start {

        private static final long time = System.nanoTime();
    }

    private static final long SECOND_TO_NANO = 1_000_000_000L;
    private static final double NANO_TO_SECOND = 1. / SECOND_TO_NANO;
    public static final GameTime INSTANCE = new GameTime();
    private long elapsed = 0;
    private Map<Integer, TickListenerManager> tickListener = new HashMap<>();
    private Collection<TimeListener> timeListener = new LinkedList<>();

    //TODO multithreading einbaun, d.h. wie updated man z.B. den opengl render thread
    //TODO überhaupt ueberlegen wie nuetzlich nicht varying time ist
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
        for (TimeListener listener : timeListener) {
            listener.update(delta);
        }
    }

    private void updatedTickListener(long nanoSeconds) {
        for (TickListenerManager lm : tickListener.values()) {
            lm.update(elapsed, nanoSeconds);
        }
    }

    public void addListener(int frequency, TickListener listener) {

        getTLM(frequency).addListener(listener);
    }

    public void addListener(int frequency, TickDeltaListener listener) {

        getTLM(frequency).addListener(listener);
    }

    private TickListenerManager getTLM(int freq) {
        TickListenerManager tlm = tickListener.get(freq);
        if (tlm == null) {
            tlm = new TickListenerManager(freq);
            tickListener.put(freq, tlm);
        }
        return tlm;
    }

    public void removeListener(TickListener listener) {
        for (TickListenerManager lm : tickListener.values()) {
            if (lm.removeListener(listener) && lm.isEmpty()) {
                tickListener.remove(lm.getFrequency());
                break;
            }
        }
    }

    public void removeListener(TickDeltaListener listener) {
        for (TickListenerManager lm : tickListener.values()) {
            if (lm.removeListener(listener) && lm.isEmpty()) {
                tickListener.remove(lm.getFrequency());
                break;
            }
        }
    }

    public void addListener(TimeListener listener) {
        timeListener.add(listener);
    }

    public void removeListener(TimeListener listener) {
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
