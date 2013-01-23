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
package darwin.core.gui;

import java.util.*;

import darwin.core.controls.InputController;
import darwin.core.dependencies.CoreModul;
import darwin.renderer.GraphicContext;
import darwin.renderer.dependencies.RendererModul;
import darwin.resourcehandling.dependencies.ResourceHandlingModul;
import darwin.util.logging.*;
import darwin.util.misc.RuntimeUtil;

import com.google.inject.*;
import com.jogamp.newt.Window;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.opengl.util.*;
import javax.inject.Inject;
import javax.media.opengl.*;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

/**
 *
 * @author daniel
 */
public class Client {

    private AnimatorBase animator;
    @InjectLogger
    private Logger logger = NOPLogger.NOP_LOGGER;
    private final Collection<ShutdownListener> shutdownlistener = new LinkedList<>();
    private final GraphicContext gc;
    private final List<GLEventListener> glListeners = new ArrayList<>();
    private final List<MouseListener> mouseListeners = new ArrayList<>();
    private static Injector INJECTOR;

    public static Client createClient() {
        return getInjector(false).getInstance(Client.class);
    }

    public static Client createClient(boolean debug) {
        return getInjector(debug).getInstance(Client.class);
    }

    public synchronized static Injector getInjector(boolean debug) {
        if (INJECTOR == null) {
            Stage stage = (RuntimeUtil.IS_DEBUGGING || debug) ? Stage.DEVELOPMENT : Stage.PRODUCTION;
            INJECTOR = Guice.createInjector(stage, new CoreModul(),
                                            new RendererModul(),
                                            new LoggingModul(),
                                            new ResourceHandlingModul());
        }
        return INJECTOR;
    }

    @Inject
    public Client(GraphicContext gc) {
        this.gc = gc;
    }

    public void iniClient() throws InstantiationException {
        try {
            gc.iniContext();
            for (GLEventListener l : glListeners) {
                gc.getGLWindow().addGLEventListener(l);
            }
            for (MouseListener l : mouseListeners) {
                gc.getGLWindow().addMouseListener(l);
            }
        } catch (GLException ex) {
            logger.error("Couldn't Initialize a graphic context!", ex);
            shutdown();
            throw new InstantiationException("Couldn't create an OpenGL Context");
        }

        animator = new Animator(gc.getGLWindow());
        animator.start();
    }

    public void shutdown() {
        if (animator != null) {
            animator.stop();
        }

        for (ShutdownListener listener : shutdownlistener) {
            listener.doShutDown();
        }
    }

    public void addShutdownListener(ShutdownListener listener) {
        shutdownlistener.add(listener);
    }

    public void removeShutdownListner(ShutdownListener lister) {
        shutdownlistener.remove(lister);
    }

    public <T extends GLEventListener> T addGLEventListener(Class<T> listener) {
        T t = INJECTOR.getInstance(listener);
        addGLEventListener(t);
        return t;
    }

    public void addGLEventListener(GLEventListener listener) {
        if (gc.isInitialized()) {
            gc.getGLWindow().addGLEventListener(listener);
        } else {
            glListeners.add(listener);
        }
    }

    public void removeGLEventListener(GLEventListener listener) {
        if (gc.isInitialized()) {
            gc.getGLWindow().removeGLEventListener(listener);
        } else {
            glListeners.remove(listener);
        }
    }

    public void addMouseListener(InputController controller) {
        if (gc.isInitialized()) {
            gc.getGLWindow().addMouseListener(controller);
        } else {
            mouseListeners.add(controller);
        }
    }

    public void addMouseListener(MouseListener controller) {
        if (gc.isInitialized()) {
            gc.getGLWindow().addMouseListener(controller);
        } else {
            mouseListeners.add(controller);
        }
    }

    Window getWindow() {
        return gc.getGLWindow();
    }
}
