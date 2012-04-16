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

/**
 * Class to handle all time relevant management. There are different Listeners types
 * which can be managed. Each time the update method get called, every listener gets
 * notified about the progress the time has made since the last invocation.
 *
 * DeltaListener: gets notified about the delta of the time in seconds(double)
 * StepListener: every update,
 * @author daniel
 */
public class GameTime {

    private static class Start {

        private static final long time = System.nanoTime();
    }

    private static final long SECOND_TO_NANO = 1_000_000_000L;
    private static final double NANO_TO_SECOND = 1. / SECOND_TO_NANO;
    private long elapsed = 0, virtualElapsed = 0;
    private double scale = 1;
    private Map<Integer, StepListenerManager> stepListener = new HashMap<>();
    private Collection<DeltaListener> timeListener = new LinkedList<>();

    public void update() {
        long start = Start.time;

        long now = System.nanoTime();

        long delta = now - start - elapsed;
        long virtDelta = (long) (delta * scale);

        updatedTimeListener(virtDelta);
        updatedStepListener(virtDelta);

        virtualElapsed += virtDelta;
        elapsed += delta;
    }

    private void updatedTimeListener(long nanoSeconds) {
        double delta = nanoSeconds * NANO_TO_SECOND;
        for (DeltaListener listener : timeListener) {
            listener.update(delta);
        }
    }

    private void updatedStepListener(long nanoSeconds) {
        for (StepListenerManager lm : stepListener.values()) {
            lm.update(elapsed, nanoSeconds);
        }
    }

    public void addListener(int frequency, StepListener listener) {

        getStepManager(frequency).addListener(listener);
    }

    private StepListenerManager getStepManager(int freq) {
        StepListenerManager tlm = stepListener.get(freq);
        if (tlm == null) {
            tlm = new StepListenerManager(freq);
            stepListener.put(freq, tlm);
        }
        return tlm;
    }

    public void removeListener(StepListener listener) {
        for (StepListenerManager lm : stepListener.values()) {
            if (lm.removeListener(listener) && lm.isEmpty()) {
                stepListener.remove(lm.getFrequency());
                break;
            }
        }
    }

    public void addListener(DeltaListener listener) {
        timeListener.add(listener);
    }

    public void removeListener(DeltaListener listener) {
        timeListener.remove(listener);
    }

    public double getElapsedTime() {
        return virtualElapsed * NANO_TO_SECOND;
    }

    /**
     *
     * @param frequency of ticks in herz
     * @return
     */
    public long getElapsedSteps(int frequency) {
        return virtualElapsed / (frequency * SECOND_TO_NANO);
    }

    public void setTimeScale(double scale)
    {
        this.scale = scale;
    }

    public double getTimeScale()
    {
        return scale;
    }
}
