/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under the terms of the GNU General Public License as published by  *  the Free Software Foundation, either version 3 of the License, or  *  (at your option) any later version.  *   *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.core.gui;

import java.awt.*;
import java.awt.image.*;

import javax.swing.JPanel;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class ColorLUTPanel extends JPanel
{
    private static final int size = 32;
    private static final int height = 2;
    private static final int width = size / height;
    private static final int mul = 256 / size;
    private static final BufferedImage image =
                                       new BufferedImage(size * width,
                                                         size * height,
                                                         BufferedImage.TYPE_INT_RGB);
    static {
        WritableRaster r = image.getRaster();
        for (int z = 0; z < size; ++z) {
            int xs = size * (z % width);
            int ys = size * (z / width);
            for (int x = 0; x < size; ++x) {
                for (int y = 0; y < size; ++y) {
                    r.setPixel(xs + x, ys + y,
                               new int[]{x * mul, y * mul, z * mul});
                }
            }
        }
    }

    public ColorLUTPanel() {
        setOpaque(false);
        setMaximumSize(new Dimension(image.getWidth(), image.getHeight()));
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(image, 0, 0, null);
    }
}
