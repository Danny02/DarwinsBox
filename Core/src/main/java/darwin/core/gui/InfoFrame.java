/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under the terms of the GNU General Public License as published by  *  the Free Software Foundation, either version 3 of the License, or  *  (at your option) any later version.  *   *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.core.gui;

import java.awt.Dimension;
import javax.swing.JFrame;

import darwin.renderer.util.memory.MemoryInfo;
import darwin.renderer.util.memory.PerformanceView;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class InfoFrame extends JFrame implements PerformanceView
{
    private InfoPanel panel = new InfoPanel(1);

    public InfoFrame() {
        setMinimumSize(new Dimension(250, 160));
        getContentPane().add(panel);
        pack();
        setVisible(true);
    }

    public void setFPS(double fps) {

        panel.setFPS(fps);
    }

    public void setMemInfo(MemoryInfo meminfo) {
        panel.setMemInfo(meminfo);
    }
}
