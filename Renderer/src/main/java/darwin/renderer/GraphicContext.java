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
package darwin.renderer;

import com.jogamp.newt.opengl.GLWindow;
import javax.media.opengl.*;

/**
 * Class which manages the creation of a OpenGL Context.
 * A GLWindow is used for holding the Context, which have to get
 * initialized before using.
 * A DEFAULT Instance is provided which uses the latest OpenGL Profile
 * available. This instance can be accessed over the static accessors.
 * @author daniel
 */
public final class GraphicContext
{
    private static final GraphicContext DEFAULT = new GraphicContext();
    private final String glprofil;
    private GLWindow window;

    private GraphicContext()
    {
        glprofil = null;
    }

    /**
     * Creates a Graphic Context with the named OpenGL profile
     * @param profil
     * OpenGL profile String as in GLProfile specified.
     */
    public GraphicContext(String profil)
    {
        glprofil = profil;
    }

    private GLCapabilitiesImmutable getCapabilities(GLProfile profile)
    {
        GLCapabilities capabilitys = new GLCapabilities(profile);
//        c.setSampleBuffers(true);
//        c.setNumSamples(16);
//        capabilitys.setHardwareAccelerated(true);
//        capabilitys.setDoubleBuffered(true);
//        capabilitys.setBackgroundOpaque(false);

        return capabilitys;
    }

    /**
     * Tries to create a OpenGL Context.
     * @throws GLException
     * when anything prevents the creation, the exception holds the underlying
     * problem in its "Throwable" field.
     */
    synchronized public void iniContext() throws GLException
    {
        assert window == null : "The Context is already initialized!";

        GLProfile.initSingleton();
        GLProfile profile = null;
        try {
            if (glprofil == null) {
                profile = GLProfile.getMaximum();
            } else {
                profile = GLProfile.get(glprofil);
            }
        } catch (Throwable t) {
            throw new GLException("Couldn't initialize OpenGL!", t);
        }

        window = GLWindow.create(getCapabilities(profile));
    }

    private static boolean currentThread(GLWindow w)
    {
        assert initialization(w);
        assert w.getContext().isCurrent() : "No OpenGL context current on this thread";
        return true;
    }

    private static boolean initialization(GLWindow w)
    {
        assert w != null : "Context is not initialized";
        return true;
    }

    /**
     * gets the GL object of the context
     * @return
     */
    public GL getInstantGL()
    {
        assert currentThread(window);
        return window.getGL();
    }

    /**
     * gets the GLWindow object, which holds the context
     * @return
     */
    public GLWindow getInstantGLWindow()
    {
        assert initialization(window);
        return window;
    }

    /**
     * @return
     * the GL object of the default context
     */
    public static GL getGL()
    {
        return DEFAULT.getInstantGL();
    }

    /**
     *
     * @return
     * the GLWindow which holds the default context
     */
    public static GLWindow getGLWindow()
    {
        return DEFAULT.getInstantGLWindow();
    }

    /**
     * initialize the default context
     * @throws GLException
     */
    public static void iniDefault() throws GLException
    {
        DEFAULT.iniContext();
    }
}
