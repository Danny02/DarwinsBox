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

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLRunnable;

import static darwin.renderer.GraphicContext.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
final class NVidiaMemoryInfo extends MemoryInfo
{

    private static final int DEDICATED_VIDMEM_NVX = 0x9047;
    private static final int CURRENT_AVAILABLE_VIDMEM_NVX = 0x9049;
    private static final int TOTAL_AVAILABLE_MEMORY_NVX = 0x9048;
    private static final int total_mem = getData(TOTAL_AVAILABLE_MEMORY_NVX);

    @Override
    public int getTotalMemory()
    {
        return total_mem;
    }

    @Override
    public int getCurrentMemory()
    {
        return getData(CURRENT_AVAILABLE_VIDMEM_NVX);
    }

    @Override
    public double getFreeRatio()
    {
        return (double) getCurrentMemory() / total_mem;
    }

    @Override
    public String getStatus()
    {
        StringBuilder status = new StringBuilder();
        status.append("Total available memory available for allocations: ");
        appendMiByte(status, getTotalMemory());

        status.append("Dedicated video memory of the GPU: ");
        appendMiByte(status, getData(DEDICATED_VIDMEM_NVX));

        status.append("Current available dedicated video memory: ");
        appendMiByte(status, getCurrentMemory());
        status.append('\n');

        return status.toString();
    }

    private void appendMiByte(StringBuilder buffer, int b)
    {
        buffer.append(b / 1024.).append("MiByte\n");
    }

    private static int getData(final int flag)
    {
        final int[] result = new int[1];

        getGLWindow().invoke(true, new GLRunnable()
        {

            @Override
            public boolean run(GLAutoDrawable drawable)
            {
                getGL().glGetIntegerv(flag, result, 0);
                return true;
            }
        });
        return result[0];
    }
}
