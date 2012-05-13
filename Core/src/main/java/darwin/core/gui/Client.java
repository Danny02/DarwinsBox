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

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.AnimatorBase;
import java.util.Collection;
import java.util.LinkedList;
import javax.media.opengl.GLException;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

import darwin.util.logging.ExceptionHandler;

import static darwin.renderer.GraphicContext.*;

/**
 *
 * @author daniel
 */
public class Client
{

    private AnimatorBase animator;
    private Logger log = Logger.getLogger("darwin");
    private final Collection<ShutdownListener> shutdownlistener = new LinkedList<>();

    public Client()
    {
        final ExceptionHandler el = new ExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(el);
    }

    public void iniClient() throws InstantiationException
    {
        try {
            iniDefault();
        } catch (GLException ex) {
            log.fatal("Couldn't Initialize a graphic context!", ex);
            shutdown();
            throw new InstantiationException("Couldn't create an OpenGL Context");
        }

        animator = new Animator(getGLWindow());
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
}
