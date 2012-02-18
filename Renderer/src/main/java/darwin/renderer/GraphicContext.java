/*
 * Copyright (C) 2011 daniel
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
package darwin.renderer;

import com.jogamp.newt.opengl.GLWindow;
import javax.media.opengl.*;
import org.apache.log4j.Logger;


/**
 *
 * @author daniel
 */
public final class GraphicContext
{

    private static class Log
    {

        private static final Logger ger = Logger.getLogger(GraphicContext.class);
    }

    public static final GraphicContext INSTANCE = new GraphicContext();
    private GLWindow window;

    private GraphicContext()
    {
    }

    private GLCapabilitiesImmutable getCapabilities(GLProfile profile)
    {
        GLCapabilities capabilitys = new GLCapabilities(profile);
//        c.setSampleBuffers(true);
//        c.setNumSamples(16);
//        capabilitys.setHardwareAccelerated(true);
//        capabilitys.setDoubleBuffered(true);
        capabilitys.setBackgroundOpaque(false);

        return capabilitys;
    }

    synchronized private void ini() throws GLException
    {
        assert window == null : "The Context is already initialized!";

        GLProfile.initSingleton();
        GLProfile profile = null;
        try {
            profile = GLProfile.getMaximum();
        } catch (Throwable t) {
            throw new GLException("Couldn't initialize OpenGL!", t);
        }

        window = GLWindow.create(getCapabilities(profile));
    }

    public static GL getGL()
    {
        assert INSTANCE.window != null : "Context is not initialized";
        assert INSTANCE.window.getContext().isCurrent() : "No OpenGL context current on this thread";
        return INSTANCE.window.getGL();
    }

    public static void iniContext() throws GLException
    {
        INSTANCE.ini();
    }

    public static GLWindow getGLWindow()
    {
        assert INSTANCE.window != null : "Context is not initialized";
        return INSTANCE.window;
    }
}
