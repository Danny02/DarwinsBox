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

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import static darwin.renderer.GraphicContext.*;

/**
 * Kein Fenster im eigentlichen Sinne, sondern mehr eine Ansammlung der Objekte,
 * aus denen das Fenster aufgebaut ist. Dient zur Initialisierung des Renderers
 * und dem Zugriff auf dessen Komponenten.
 * <p/>
 * @author Daniel Heinrich
 * <p/>
 */
public class ClientWindow implements ShutdownListener
{

    private final Client client;
    private final int width, height;
    private final boolean fullscreen;

    public ClientWindow(int xSize, int ySize, boolean fullscreen)
    {
        width = xSize;
        height = ySize;
        this.fullscreen = fullscreen;

        client = new Client();
        client.addShutdownListener(this);
        client.addLogAppender(new AppenderSkeleton()
        {

            @Override
            protected void append(LoggingEvent event)
            {
                if (event.getLevel() == Level.FATAL) {
                    ThrowableInformation ti = event.getThrowableInformation();
                    if (ti != null) {
                        ti.getThrowable().printStackTrace();
                    }
                    doShutDown();
                }
            }

            @Override
            public void close()
            {
            }

            @Override
            public boolean requiresLayout()
            {
                return false;
            }
        });
    }

    public void startUp() throws InstantiationException
    {
        client.iniClient();
        GLWindow win = getGLWindow();
        win.setSize(width, height);
        win.setVisible(true);

        win.addWindowListener(new WindowAdapter()
        {

            @Override
            public void windowDestroyed(WindowEvent arg0)
            {
                doShutDown();
            }
        });
    }

    @Override
    public void doShutDown()
    {
        /*
         * Zur Sicherheit in eigenem Thread ausführen, da das Swing-System
         * blockieren könnte.
         */
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                client.shutdown();
                System.exit(0);
            }
        }).start();
    }
}
