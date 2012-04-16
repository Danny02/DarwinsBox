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
