/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.core.gui;

import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.AnimatorBase;
import java.awt.Container;
import java.util.Collection;
import java.util.LinkedList;
import javax.media.opengl.GLException;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

import darwin.core.logging.ExceptionHandler;

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
            iniContext();
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
