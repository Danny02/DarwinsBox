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
package darwin.geometrie.factorys.grids;

import java.util.*;

import darwin.geometrie.data.VertexBuffer;
import darwin.geometrie.factorys.*;
import darwin.util.math.base.Vector;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class CellFactory
{
    private final int tessfactor;
    private final QuadFactory qfactory;
    private List<Cell> cells = new LinkedList<>();

    public CellFactory(int tessfactor) {
        this.tessfactor = tessfactor;
        qfactory = new QuadFactory(1. / tessfactor);
    }

    public void createCell(Vector position) {
        Quad[] quads = new Quad[tessfactor * tessfactor];
        double size = 1. / tessfactor;
        for (int x = 0; x < tessfactor; x++)
            for (int y = 0; y < tessfactor; y++)
                quads[tessfactor * x + y] = qfactory.createQuad(position.add(
                        new Vector(size * x, -size * y)));
        cells.add(new Cell(quads, tessfactor));
    }

    public int createCells(Vector[] positions) {
        for (Vector v : positions) {
            qfactory.precachePosition(v);
            qfactory.precachePosition(v.add(new Vector(tessfactor, 0)));
            qfactory.precachePosition(v.add(new Vector(tessfactor, tessfactor)));
            qfactory.precachePosition(v.add(new Vector(0, tessfactor)));
        }

        int r = qfactory.getVertexCount();

        for (Vector v : positions)
            createCell(v);

        return r;
    }

    public int getVertexCount() {
        return qfactory.getVertexCount();
    }

    public void fillVBufferPerVertex(VertexBuffer vb, PerVertexFiller pvf) {
        int vc = getVertexCount();
        if (vb.getVcount() < vc)
            for (int i = vc - vb.getVcount(); i != 0; --i)
                vb.addVertex();

        Vector[] pos = qfactory.getPositions();
        for (int i = 0; i < pos.length; ++i)
            pvf.fill(vb.getVertex(i), pos[i]);
    }

    public void fillVBufferPerCell(VertexBuffer vb, PerCellFiller pcf) {
        int vc = getVertexCount();
        if (vb.getVcount() < vc)
            for (int i = vc - vb.getVcount(); i != 0; --i)
                vb.addVertex();

        Set<Integer> ready = new HashSet<>();
        for (int j = 0; j < cells.size(); j++) {
            Cell cell = cells.get(j);
            int[] corners = cell.getCorners();
            for (int i = 0; i < corners.length; i++)
                if (ready.add(corners[i]))
                    pcf.fill(vb.getVertex(corners[i]), j, i);
            cell.interpolate(vb);
        }
    }

    public int getTessfactor() {
        return tessfactor;
    }

    public Cell[] getCells() {
        Cell[] q = new Cell[cells.size()];
        return cells.toArray(q);
    }
}
