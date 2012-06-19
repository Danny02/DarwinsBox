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
package darwin.renderer.dependencies;

import javax.inject.*;
import javax.media.opengl.*;

import darwin.renderer.GraphicContext;
import darwin.renderer.util.memory.*;

/**
 *
 * @author daniel
 */
public class MemoryInfoProvider implements Provider<MemoryInfo>
{

    public enum Vendor
    {

        AMD("GL_ATI_meminfo"),
        NVIDIA("GL_NVX_gpu_memory_info"),
        DUMMY("");
        public final String vendorExtension;

        private Vendor(String vendorExtension)
        {
            this.vendorExtension = vendorExtension;
        }
    }
    private final GraphicContext gc;

    @Inject
    public MemoryInfoProvider(GraphicContext gc)
    {
        this.gc = gc;
    }

    @Override
    public MemoryInfo get()
    {
        Vendor v = getVendor();
        switch (v) {
            case AMD:
                return new ATIMemoryInfo(gc);
            case NVIDIA:
                return new NVidiaMemoryInfo(gc);
            default:
                return new DummyMemInfo();
        }

    }

    private Vendor getVendor()
    {
        final Vendor[] result = new Vendor[1];
        gc.getGLWindow().invoke(true, new GLRunnable()
        {

            @Override
            public boolean run(GLAutoDrawable drawable)
            {
                for (Vendor v : Vendor.values()) {
                    if (drawable.getGL().isExtensionAvailable(v.vendorExtension)) {
                        result[0] = v;
                        break;
                    }
                }
                return true;
            }
        });

        return result[0] == null ? Vendor.DUMMY : result[0];
    }
}
