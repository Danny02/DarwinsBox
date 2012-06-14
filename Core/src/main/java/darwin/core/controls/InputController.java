/*
 *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a clone of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.core.controls;

import java.awt.*;
import java.awt.event.*;

import darwin.util.math.base.Line;
import darwin.util.math.base.matrix.Matrix4;
import darwin.util.math.base.vector.*;
import darwin.util.math.util.MatrixCache;

/**
 *
 * @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class InputController extends MouseAdapter
{
    private final Point last = new Point();
    private final ViewModel view;
    private final WorldModel world;
    private final MatrixCache matrice;

    public InputController(ViewModel view, WorldModel world, MatrixCache matrice)
    {
        this.view = view;
        this.world = world;
        this.matrice = matrice;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        Component c = e.getComponent();
        float x = 2f * e.getX() / c.getWidth() - 1f;
        float y = -2f * e.getY() / c.getHeight() - 1f;

        Matrix4 inverse = matrice.getViewProjectionInverse();
        Vector near, far;
        near = inverse.mult(new GenericVector(x, y, -1f, 1f));
        far = inverse.mult(new GenericVector(x, y, 1f, 1f));

        float d = 1f / near.getCoords()[3];
        near.mul(d);
        far.mul(d);
        if (world != null) {
            world.select(new Line(near.toVector3(), far.toVector3()));
        }
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        Component c = e.getComponent();
        double x = (double) (last.x - e.getX()) / c.getWidth();
        double y = (double) (last.y - e.getY()) / c.getHeight();
        last.setLocation(e.getPoint());
        if (view != null) {
            view.dragged(x, y);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        last.setLocation(e.getPoint());
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if (view != null) {
            view.steps(e.getWheelRotation(), e.isControlDown(), e.isShiftDown());
        }
    }
}
