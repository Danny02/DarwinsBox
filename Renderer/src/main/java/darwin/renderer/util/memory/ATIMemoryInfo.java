/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer.util.memory;

import static darwin.renderer.GraphicContext.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class ATIMemoryInfo implements MemoryInfo
{
    private static final int VBO_FREE_MEMORY_ATI = 0x87FB;
    private static final int TEXTURE_FREE_MEMORY_ATI = 0x87FC;
    private static final int RENDERBUFFER_FREE_MEMORY_ATI = 0x87FD;
//    TODO man kann ned gscheit die echte Speichergröße abfragen
//    nur freien speicher beim programmstart
//    private static final int TOTAL_PHYSICAL_MEMORY_ATI = 0x87FE;
    private static final int total_mem = getData(TEXTURE_FREE_MEMORY_ATI)[0];

    @Override
    public int getTotalMemory() {
        return total_mem;
    }

    @Override
    public int getCurrentMemory() {
        return getData(TEXTURE_FREE_MEMORY_ATI)[0];
    }

    @Override
    public double getFreeRatio() {
        return (double) getCurrentMemory() / total_mem;
    }

    /**
     * param[0] - total memory free in the pool
     * param[1] - largest available free block in the pool
     * param[2] - total auxiliary memory free
     * param[3] - largest auxiliary free block
     * @param pool
     * @return
     */
    private static int[] getData(int pool) {
        int[] result = new int[4];
        getGL().glGetIntegerv(pool, result, 0);
        return result;
    }

    @Override
    public String getStatus() {
        StringBuffer status = new StringBuffer("Total physical video memory: ");
        appendKiByte(status, total_mem);
        status.append('\n');

        status.append("--- VBO POOL ---\n");
        appendData(status, getData(VBO_FREE_MEMORY_ATI));
        status.append("--- TEXTURE POOL ---\n");
        appendData(status, getData(TEXTURE_FREE_MEMORY_ATI));
        status.append("--- RENDERBUFFER POOL ---\n");
        appendData(status, getData(RENDERBUFFER_FREE_MEMORY_ATI));

        return status.toString();
    }

    private void appendData(StringBuffer buffer, int[] data) {
        buffer.append("\ttotal memory free in the pool: ");
        appendKiByte(buffer, data[0]);
        buffer.append("\tlargest available free block in the pool: ");
        appendKiByte(buffer, data[1]);
        buffer.append("\ttotal auxiliary memory free: ");
        appendKiByte(buffer, data[2]);
        buffer.append("\tlargest auxiliary free block: ");
        appendKiByte(buffer, data[3]);
        buffer.append('\n');
    }

    private void appendKiByte(StringBuffer buffer, int b) {
        buffer.append(b);
        buffer.append("KiByte\n");
    }
}
