/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under the terms of the GNU General Public License as published by  *  the Free Software Foundation, either version 3 of the License, or  *  (at your option) any later version.  *   *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.core.gui;

//import netscape.javascript.JSException;
//import netscape.javascript.JSObject;

import com.jogamp.opengl.util.*;
import java.awt.*;
import javax.media.opengl.*;
import javax.swing.*;

//TODO JS funktionalitaet wiederherstellen, umstieg auf Rhino(OpenJDK7)
/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class ClientApplet extends JApplet implements ShutdownListener, AnimatorFactory {

    private Client client;

    @Override
    public void init() {
        client = new Client(this);
        client.addShutdownListener(this);
        setLayout(new BorderLayout());
    }

    @Override
    public void start() {
        client.iniClient(this, BorderLayout.CENTER);
    }

    @Override
    public void stop() {
        client.shutdown();
    }

    public AnimatorBase createAnimator(GLAutoDrawable draw) {
        return new FPSAnimator(draw, 60);
    }

    @Override
    public void doShutDown() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
