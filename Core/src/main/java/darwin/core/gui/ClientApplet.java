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

//import netscape.javascript.JSException;
//import netscape.javascript.JSObject;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jogamp.newt.awt.NewtCanvasAWT;
import java.awt.BorderLayout;
import javax.swing.JApplet;


//TODO JS funktionalitaet wiederherstellen, umstieg auf Rhino(OpenJDK7)
/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
@SuppressWarnings("serial")
public class ClientApplet extends JApplet implements ShutdownListener
{

    private Client client;

    @Override
    public void init()
    {
        Injector in = Guice.createInjector();
        client = in.getInstance(Client.class);
        client.addShutdownListener(this);
        setLayout(new BorderLayout());
    }

    @Override
    public void start()
    {
        try {
            client.iniClient();
            NewtCanvasAWT canvas = new NewtCanvasAWT(client.getWindow());
            add(canvas, BorderLayout.CENTER);
        } catch (InstantiationException ex) {
            doShutDown();
        }
    }

    @Override
    public void stop()
    {
        doShutDown();
    }

    @Override
    public void doShutDown()
    {
        client.shutdown();
    }
}
