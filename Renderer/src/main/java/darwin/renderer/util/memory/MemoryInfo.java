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
public abstract class MemoryInfo
{

    public static final MemoryInfo INSTANCE;

    static {
        if (checkExtension("GL_ATI_meminfo")) {
            INSTANCE = new ATIMemoryInfo();
        } else if (checkExtension("GL_NVX_gpu_memory_info")) {
            INSTANCE = new NVidiaMemoryInfo();
        } else {
            INSTANCE = new DummyMemInfo();
        }
    }

    /**
     * total available video memory of the GPU
     * <p/>
     * @return Memory in KiByte
     */
    public abstract int getTotalMemory();

    /**
     * Memory which is currently available
     * <p/>
     * @return Memory in KiByte
     */
    public abstract int getCurrentMemory();

    /**
     * Vendor specific memory Info
     * <p/>
     * @return
     */
    public abstract String getStatus();

    /**
     * Percentage of free video memory
     * <p/>
     * @return
     */
    public abstract double getFreeRatio();

    public static boolean canCollectData()
    {
        return !(INSTANCE instanceof DummyMemInfo);
    }

    private static boolean checkExtension(final String ex)
    {
        final boolean[] result = new boolean[1];
        getGLWindow().invoke(true, new GLRunnable() {
            @Override
            public boolean run(GLAutoDrawable drawable)
            {
                result[0] = drawable.getGL().isExtensionAvailable(ex);
                return true;
            }
        });
        return result[0];
    }
}
