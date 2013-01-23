/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under the terms of the GNU General Public License as published by  *  the Free Software Foundation, either version 3 of the License, or  *  (at your option) any later version.  *   *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.core.gui;

import java.awt.*;
import java.nio.ByteBuffer;
import javax.swing.JPanel;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class HistogrammPanel extends JPanel
{
    private final int[][] bars = new int[256][3];

    public HistogrammPanel() {
        setOpaque(false);
    }

    public void update(ByteBuffer buffer) {
        buffer.rewind();
        for (int i = 0; buffer.remaining() > 3; i++) {
            bars[i][0] = buffer.get() & 0xFF;
            bars[i][1] = buffer.get() & 0xFF;
            bars[i][2] = buffer.get() & 0xFF;
            buffer.get();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.SrcOver.derive(0.5f));
        Composite oldcomp = g2d.getComposite();
        g2d.setColor(Color.WHITE);
//        g2d.clearRect(0, 0, getWidth(), getHeight());
        for (int i = 0; i < 256; ++i) {
            g2d.setColor(Color.red);
            g2d.fillRect(i, 0, 1, bars[i][0]);
            g2d.setColor(Color.green);
            g2d.fillRect(i, 0, 1, bars[i][1]);
            g2d.setColor(Color.blue);
            g2d.fillRect(i, 0, 1, bars[i][2]);
        }


        super.paintComponent(g2d);
        g2d.setComposite(oldcomp);
    }
}
