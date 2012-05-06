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

import darwin.renderer.GraphicContext;
import static darwin.renderer.GraphicContext.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public final class ATIMemoryInfo implements MemoryInfo
{

    private static final int VBO_FREE_MEMORY_ATI = 0x87FB;
    private static final int TEXTURE_FREE_MEMORY_ATI = 0x87FC;
    private static final int RENDERBUFFER_FREE_MEMORY_ATI = 0x87FD;
//    TODO man kann ned gscheit die echte Speichergröße abfragen
//    nur freien speicher beim programmstart
//    private static final int TOTAL_PHYSICAL_MEMORY_ATI = 0x87FE;
    private final int totalMem;
    private final GraphicContext gc;

    public ATIMemoryInfo(GraphicContext gc)
    {
        totalMem = getData(TEXTURE_FREE_MEMORY_ATI)[0];
        this.gc = gc;
    }

    @Override
    public int getTotalMemory()
    {
        return totalMem;
    }

    @Override
    public int getCurrentMemory()
    {
        return getData(TEXTURE_FREE_MEMORY_ATI)[0];
    }

    @Override
    public double getFreeRatio()
    {
        return (double) getCurrentMemory() / totalMem;
    }

    /**
     * param[0] - total memory free in the pool param[1] - largest available
     * free block in the pool param[2] - total auxiliary memory free param[3] -
     * largest auxiliary free block
     * <p/>
     * @param pool < p/>
     * <p/>
     * @return
     */
    private int[] getData(final int pool)
    {
        final int[] result = new int[4];
        gc.getGLWindow().invoke(true, new GLRunnable()
        {

            @Override
            public boolean run(GLAutoDrawable drawable)
            {
                gc.getGL().glGetIntegerv(pool, result, 0);
                return true;
            }
        });
        return result;
    }

    @Override
    public String getStatus()
    {
        StringBuffer status = new StringBuffer("Total physical video memory: ");
        appendMiByte(status, getTotalMemory());
        status.append('\n');

        status.append("--- VBO POOL ---\n");
        appendData(status, getData(VBO_FREE_MEMORY_ATI));
        status.append("--- TEXTURE POOL ---\n");
        appendData(status, getData(TEXTURE_FREE_MEMORY_ATI));
        status.append("--- RENDERBUFFER POOL ---\n");
        appendData(status, getData(RENDERBUFFER_FREE_MEMORY_ATI));

        return status.toString();
    }

    private void appendData(StringBuffer buffer, int[] data)
    {
        buffer.append("\ttotal memory free in the pool: ");
        appendMiByte(buffer, data[0]);
        buffer.append("\tlargest available free block in the pool: ");
        appendMiByte(buffer, data[1]);
        buffer.append("\ttotal auxiliary memory free: ");
        appendMiByte(buffer, data[2]);
        buffer.append("\tlargest auxiliary free block: ");
        appendMiByte(buffer, data[3]);
        buffer.append('\n');
    }

    private void appendMiByte(StringBuffer buffer, int b)
    {
        buffer.append(b / 1024.).append("MiByte\n");
    }
}
