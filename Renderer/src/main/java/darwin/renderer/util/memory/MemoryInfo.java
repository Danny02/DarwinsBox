/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
