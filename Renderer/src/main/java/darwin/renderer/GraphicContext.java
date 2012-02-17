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
public final class GraphicContext {

    private GL gl;
    private GLContext context;
    private GLPbuffer backend;
    private Thread mthread;
    private final GLCapabilitiesImmutable capas;

    private GraphicContext(GLCapabilitiesImmutable capa) {
        capas = capa;
    }

    synchronized public GLProfile ini() throws GLException {
        assert backend == null : "Context already initialized.";

        GLDrawableFactory factory = GLDrawableFactory.getFactory(GLProfile.getMaximum());
        if (!factory.canCreateGLPbuffer(null)) {
            throw new GLException("Das System unterstützt keine pbuffer.");
        }
        backend = factory.createGLPbuffer(null, capas, new DefaultGLCapabilitiesChooser(), 1, 1, null);
        backend.releaseTexture();
        context = backend.getContext();
        gl = context.getGL();
        mthread = Thread.currentThread();
        return gl.getGLProfile();
    }

    public void doAsserts() {
        assert mthread == Thread.currentThread() : "No OpenGL context current on this thread";
        assert backend != null : "Context is not initialized";
    }

    /**
     * sets the render thread to the calling thread if it is the current on the context.
     *
     * should be called after context.makeCurrent() calls
     */
    public void resetRenderThread() {
        if (context.isCurrent()) {
            mthread = Thread.currentThread();
        }
    }

    public synchronized void destroy() {
        assert backend != null : "Can only destroy already initialized Context.";
        backend.destroy();
        backend = null;
    }

    private static final GraphicContext gcontext;

    static {
        GLCapabilities CAPABILITIES = new GLCapabilities(GLProfile.getMaximum());
        CAPABILITIES.setHardwareAccelerated(true);
        CAPABILITIES.setDoubleBuffered(true);
        gcontext = new GraphicContext(CAPABILITIES);
    }

    public static GL getGL() {
        gcontext.doAsserts();
        return gcontext.gl;
    }

    public static GL2GL3 getGL2GL3()
    {
        return getGL().getGL2GL3();
    }

    public static void iniContext() throws GLException {
        gcontext.ini();

    }

    public static GLContext getContext() {
        return gcontext.context;
    }

    public static GLCapabilitiesImmutable getCapabilities() {
        return gcontext.capas;
    }
}
