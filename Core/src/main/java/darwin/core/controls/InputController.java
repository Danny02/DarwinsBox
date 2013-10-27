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

import darwin.util.math.base.Line;
import darwin.util.math.base.matrix.Matrix4;
import darwin.util.math.base.vector.*;
import darwin.util.math.util.MatType;
import darwin.util.math.util.MatrixCache;

import com.jogamp.newt.Window;
import com.jogamp.newt.event.*;

/**
 *
 * @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class InputController implements MouseListener {

    private int lastX, lastY;
    private final ViewModel view;
    private final WorldModel world;
    private final MatrixCache matrice;

    public InputController(ViewModel view, WorldModel world, MatrixCache matrice) {
        this.view = view;
        this.world = world;
        this.matrice = matrice;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (world == null) {
            return;
        }
        Window w = (Window) e.getSource();
        float x = 2f * e.getX() / w.getWidth() - 1f;
        float y = -2f * e.getY() / w.getHeight() - 1f;

        Matrix4 inverse = matrice.getViewProjectionInverse();
        Vector near, far;
        near = inverse.mult(new GenericVector(x, y, -1f, 1f));
        far = inverse.mult(new GenericVector(x, y, 1f, 1f));

        float d = 1f / near.getCoords()[3];
        near.mul(d);
        far.mul(d);
        world.select(new Line(near.toVector3(), far.toVector3()));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (view != null) {
            Window w = (Window) e.getSource();
            float x = (float) (lastX - e.getX()) / w.getWidth();
            float y = (float) (lastY - e.getY()) / w.getHeight();
            view.dragged(x, y);
            matrice.fireChange(MatType.VIEW);
        }
        mouseMoved(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();
    }

    @Override
    public void mouseWheelMoved(MouseEvent e) {
        if (view != null) {
            view.steps(e.getWheelRotation(), e.isControlDown(), e.isShiftDown());
            matrice.fireChange(MatType.VIEW);
        }
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
    }
}
