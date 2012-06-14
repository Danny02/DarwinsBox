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

import com.jogamp.newt.Window;
import com.jogamp.opengl.util.*;
import java.util.*;
import javax.inject.Inject;
import javax.media.opengl.*;
import org.apache.log4j.*;

import darwin.renderer.GraphicContext;
import darwin.util.logging.ExceptionHandler;

/**
 *
 * @author daniel
 */
public class Client
{
    private AnimatorBase animator;
    private final Logger log = Logger.getLogger("darwin");
    private final Collection<ShutdownListener> shutdownlistener = new LinkedList<>();
    private final GraphicContext gc;

    @Inject
    public Client(GraphicContext gc)
    {
        this.gc = gc;
        final ExceptionHandler el = new ExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(el);
    }

    public void iniClient() throws InstantiationException
    {
        try {
            gc.iniContext();
        } catch (GLException ex) {
            log.fatal("Couldn't Initialize a graphic context!", ex);
            shutdown();
            throw new InstantiationException("Couldn't create an OpenGL Context");
        }

        animator = new Animator(gc.getGLWindow());
        animator.start();
    }

    public void shutdown()
    {
        if (animator != null) {
            animator.stop();
        }

        for (ShutdownListener listener : shutdownlistener) {
            listener.doShutDown();
        }
    }

    public void addShutdownListener(ShutdownListener listener)
    {
        shutdownlistener.add(listener);
    }

    public void removeShutdownListner(ShutdownListener lister)
    {
        shutdownlistener.remove(lister);
    }

    public synchronized void addLogAppender(Appender newAppender)
    {
        log.addAppender(newAppender);
    }

    public void addGLEventListener(GLEventListener listener)
    {
        gc.getGLWindow().addGLEventListener(listener);
    }

    public void removeGLEventListener(GLEventListener listener)
    {
        gc.getGLWindow().removeGLEventListener(listener);
    }

    public Window getWindow()
    {
        return gc.getGLWindow();
    }
}
