/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.renderer.util.memory;

import static de.dheinrich.darwin.renderer.GraphicContext.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public interface MemoryInfo {

    public static final MemoryInfo INSTANCE = getGL().isExtensionAvailable("GL_ATI_meminfo")
            ? new ATIMemoryInfo() : getGL().isExtensionAvailable("GL_NVX_gpu_memory_info")
            ? new NVidiaMemoryInfo() : null;

    /**
     * total available video memory of the GPU
     * @return
     * Memory in KiByte
     */
    public int getTotalMemory();

    /**
     * Memory which is currently available
     * @return
     * Memory in KiByte
     */
    public int getCurrentMemory();

    /**
     * Vendor specific memory Info
     * @return
     */
    public String getStatus();

    /**
     * Percentage of free video memory
     * @return
     */
    public double getFreeRatio();
}
