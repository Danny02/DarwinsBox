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
import javax.annotation.Nullable;
import javax.inject.*;
import javax.media.opengl.*;

/**
 * Class which manages the creation of a OpenGL Context. A GLWindow is used for
 * holding the Context, which have to get initialized before using. A DEFAULT
 * Instance is provided which uses the latest OpenGL Profile available. This
 * instance can be accessed over the static accessors.
 * <p/>
 * @author daniel
 */
@Singleton
public final class GraphicContext
{
    private final String glProfil;
    private GLWindow window;
    private boolean initialized = false;

    @Inject
    public GraphicContext(@Nullable @Named("GL_Profile") String profil)
    {
        glProfil = profil;
    }

    /**
     * Tries to create a OpenGL Context.
     * <p/>
     * @throws GLException when anything prevents the creation, the exception
     *                     holds the underlying problem in its "Throwable" field.
     */
    synchronized public void iniContext() throws GLException
    {
        assert initialized == false : "The Context is already initialized!";

        GLProfile.initSingleton();
        GLProfile profile = null;
        try {
            if (glProfil == null) {
                profile = GLProfile.getMaximum(true);
            } else {
                profile = GLProfile.get(glProfil);
            }
        } catch (Throwable t) {
            throw new GLException("Couldn't initialize OpenGL!", t);
        }

        window = GLWindow.create(getCapabilities(profile));
        initialized = true;
    }

    /**
     * gets the GL object of the context
     * <p/>
     * @return
     */
    public GL getGL()
    {
        assert window != null : "Context is not initialized";
        return window.getGL();
    }

    /**
     * gets the GLWindow object, which holds the context
     * <p/>
     * @return
     */
    public GLWindow getGLWindow()
    {
        assert window != null : "Context is not initialized";
        return window;
    }

    public boolean isInitialized()
    {
        return initialized;
    }

    private static GLCapabilitiesImmutable getCapabilities(GLProfile profile)
    {
        GLCapabilities capabilitys = new GLCapabilities(profile);
//        c.setSampleBuffers(true);
//        c.setNumSamples(16);
//        capabilitys.setHardwareAccelerated(true);
//        capabilitys.setDoubleBuffered(true);
//        capabilitys.setBackgroundOpaque(false);

        return capabilitys;
    }
}
