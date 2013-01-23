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
package darwin.renderer.util.memory;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public interface MemoryInfo
{
    /**
     * total available video memory of the GPU
     * <p/>
     * @return Memory in KiByte
     */
    public int getTotalMemory();

    /**
     * Memory which is currently available
     * <p/>
     * @return Memory in KiByte
     */
    public int getCurrentMemory();

    /**
     * Vendor specific memory Info
     * <p/>
     * @return
     */
    public String getStatus();

    /**
     * Percentage of free video memory
     * <p/>
     * @return
     */
    public double getFreeRatio();
}
