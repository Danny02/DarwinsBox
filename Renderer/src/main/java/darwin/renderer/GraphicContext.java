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

import javax.media.opengl.*;

/**
 *
 * @author daniel
 */
public final class GraphicContext
{

    private static final GraphicContext gcontext;

    static {
        GLCapabilities capabilitys = new GLCapabilities(GLProfile.getMaximum());
        capabilitys.setHardwareAccelerated(true);
        capabilitys.setDoubleBuffered(true);

        gcontext = new GraphicContext(capabilitys);
    }
    private GL gl;
    private GLContext context;
    private GLPbuffer backend;
    private Thread renderThreadhread;
    private final GLCapabilitiesImmutable capabilitys;

    private GraphicContext(GLCapabilitiesImmutable capa)
    {
        capabilitys = capa;
    }

    synchronized private void ini() throws GLException
    {
        GLDrawableFactory factory = GLDrawableFactory.getDesktopFactory();
        if (!factory.canCreateGLPbuffer(null)) {
            throw new GLException("The System doesn't support pbuffer!");
        }

        backend = factory.createGLPbuffer(null, capabilitys, new DefaultGLCapabilitiesChooser(), 1, 1, null);
//        backend.releaseTexture(); //TODO X11 doesn't implement this until now(16.02.12)

        renderThreadhread = Thread.currentThread();
    }

    public void doAsserts()
    {
        assert renderThreadhread == Thread.currentThread() : "No OpenGL context current on this thread";
        assert backend != null : "Context is not initialized";
    }

    /**
     * sets the render thread to the calling thread if it is the current on the
     * context.
     * <p/>
     * should be called after context.makeCurrent() calls
     */
    public void resetRenderThread()
    {
        if (context.isCurrent()) {
            renderThreadhread = Thread.currentThread();
        }
    }

    public synchronized void destroy()
    {
        assert backend != null : "Can only destroy already initialized Context.";
        backend.destroy();
        backend = null;
    }

    public static GL getGL()
    {
        gcontext.doAsserts();
        return gcontext.backend.getGL();
    }

    public static void iniContext() throws GLException
    {
        gcontext.ini();
    }

    public static GLContext getContext()
    {
        return gcontext.backend.getContext();
    }

    public static GLCapabilitiesImmutable getCapabilities()
    {
        return gcontext.capabilitys;
    }
    public static void main(String[] args)
    {
     GraphicContext.iniContext();
    }
}
