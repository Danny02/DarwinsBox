/*
 * Copyright (C) 2012 daniel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.core.timing;

/**
 *
 * @author daniel
 */
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Class to handle all time relevant management. There are different Listeners
 * types which can be managed. Each time the update method get called, every
 * listener gets notified about the progress the time has made since the last
 * invocation.
 * <p/>
 * DeltaListener: gets notified about the delta of the time in seconds(double)
 * StepListener: every update,
 * <p/>
 * @author daniel
 */
public class GameTime {

    static {
        HighPerformanceTimingFix.fix();
    }
    private static final double NANO_IN_SECOND = TimeUnit.SECONDS.toNanos(1L);
    private long startTime = -1;
    private long elapsed = 0, virtualElapsed = 0;
    private double scale = 1;
    private Map<Integer, StepListenerManager> stepListener = new HashMap<>();
    private Collection<DeltaListener> timeListener = new ArrayList<>();

    public void update() {
        if (startTime == -1) {
            startTime = System.nanoTime();
            return;
        }

        long now = System.nanoTime();

        long delta = now - startTime - elapsed;
        long virtDelta = (long) (delta * scale);
        
        virtualElapsed += virtDelta;
        elapsed += delta;
        
        updatedTimeListener(virtDelta);
        updatedStepListener(virtDelta);

    }

    private void updatedTimeListener(long nanoSeconds) {
        double delta = nanoSeconds / NANO_IN_SECOND;
        for (DeltaListener listener : new ArrayList<>(timeListener)) {
            listener.update(delta);
        }
    }

    private void updatedStepListener(long nanoSeconds) {
        for (StepListenerManager lm : new ArrayList<>(stepListener.values())) {
            lm.update(virtualElapsed, nanoSeconds);
        }
    }

    public void addListener(int frequency, StepListener listener) {
        StepListenerManager manager = stepListener.get(frequency);
        if (manager == null) {
            manager = new StepListenerManager(frequency);
            stepListener.put(frequency, manager);
        }
        manager.addListener(listener);
    }

    public void removeListener(StepListener listener) {
        for (StepListenerManager manager : stepListener.values()) {
            if (manager.removeListener(listener) && manager.isEmpty()) {
                stepListener.remove(manager.getFrequency());
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
        return virtualElapsed / NANO_IN_SECOND;
    }

    public long getElapsedSteps(int frequency) {
        return virtualElapsed / TimeUnit.SECONDS.toNanos(frequency);
    }

    public void setTimeScale(double scale) {
        this.scale = scale;
    }

    public double getTimeScale() {
        return scale;
    }
}
