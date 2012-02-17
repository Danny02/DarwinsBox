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
public class NVidiaMemoryInfo implements MemoryInfo
{
    private static final int DEDICATED_VIDMEM_NVX = 0x9047;
    private static final int TOTAL_AVAILABLE_MEMORY_NVX = 0x9048;
    private static final int CURRENT_AVAILABLE_VIDMEM_NVX = 0x9049;
    private static final int total_mem = getData(TOTAL_AVAILABLE_MEMORY_NVX);

    @Override
    public int getTotalMemory() {
        return total_mem;
    }

    @Override
    public int getCurrentMemory() {
        return getData(CURRENT_AVAILABLE_VIDMEM_NVX);
    }

    @Override
    public double getFreeRatio() {
        return (double) getCurrentMemory() / total_mem;
    }

    @Override
    public String getStatus() {
        StringBuffer status = new StringBuffer();
        status.append("Total available memory available for allocations: ");
        appendKiByte(status, total_mem);
        status.append('\n');

        status.append("Dedicated video memory of the GPU: ");
        appendKiByte(status, getData(DEDICATED_VIDMEM_NVX));
        status.append('\n');

        status.append("Current available dedicated video memory: ");
        appendKiByte(status, getData(CURRENT_AVAILABLE_VIDMEM_NVX));
        status.append('\n');

        return status.toString();
    }

    private void appendKiByte(StringBuffer buffer, int b) {
        buffer.append(b);
        buffer.append("KiByte\n");
    }

    private static int getData(int flag) {
        int[] result = new int[1];
        getGL().glGetIntegerv(flag, result, 0);
        return result[0];
    }
}
