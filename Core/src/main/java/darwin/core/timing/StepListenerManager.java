/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.core.timing;

/**
 *
 * @author daniel
 */
import java.util.Collection;
import java.util.LinkedList;

class StepListenerManager
{

    private static final long SECOND_TO_NANO = 1_000_000_000L;
    private final Collection<StepListener> tickListener = new LinkedList<>();
    private final long phase;

    StepListenerManager(int frequency)
    {
        phase = SECOND_TO_NANO / frequency;
    }

    boolean isEmpty()
    {
        return tickListener.isEmpty();
    }

    void update(long totalTime, long delta)
    {
        int ticks = (int) ((totalTime / phase) - ((totalTime - delta) / phase));
        float lerp = (float) (totalTime % phase) / phase;

        for (StepListener listener : tickListener) {
            listener.update(ticks, lerp);

        }
    }

    int getFrequency()
    {
        return (int) (phase / SECOND_TO_NANO);
    }

    void addListener(StepListener listener)
    {
        tickListener.add(listener);
    }

    boolean removeListener(StepListener listener)
    {
        return tickListener.remove(listener);
    }
}
