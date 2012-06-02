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
import darwin.util.math.base.vector.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class CellFactory
{
    private final int tessfactor;
    private final QuadFactory qfactory;
    private List<Cell> cells = new LinkedList<>();

    public CellFactory(int tessfactor)
    {
        this.tessfactor = tessfactor;
        qfactory = new QuadFactory(1f / tessfactor);
    }

    public void createCell(ImmutableVector<Vector2> position)
    {
        Quad[] quads = new Quad[tessfactor * tessfactor];
        float size = 1f / tessfactor;
        for (int x = 0; x < tessfactor; x++) {
            for (int y = 0; y < tessfactor; y++) {
                quads[tessfactor * x + y] = qfactory.createQuad(position.copy().add(
                        new Vector2(size * x, -size * y)));
            }
        }
        cells.add(new Cell(quads, tessfactor));
    }

    public int createCells(ImmutableVector<Vector2>[] positions)
    {
        for (ImmutableVector<Vector2> v : positions) {
            qfactory.precachePosition(v.copy());
            qfactory.precachePosition(new Vector2(tessfactor, 0).add(v));
            qfactory.precachePosition(new Vector2(tessfactor, tessfactor).add(v));
            qfactory.precachePosition(new Vector2(0, tessfactor).add(v));
        }

        int r = qfactory.getVertexCount();

        for (ImmutableVector<Vector2> v : positions) {
            createCell(v);
        }

        return r;
    }

    public int getVertexCount()
    {
        return qfactory.getVertexCount();
    }

    public void fillVBufferPerVertex(VertexBuffer vb, PerVertexFiller pvf)
    {
        int vc = getVertexCount();
        if (vb.getVcount() < vc) {
            for (int i = vc - vb.getVcount(); i != 0; --i) {
                vb.addVertex();
            }
        }

        ImmutableVector<Vector2>[] pos = qfactory.getPositions();
        for (int i = 0; i < pos.length; ++i) {
            pvf.fill(vb.getVertex(i), pos[i].copy());
        }
    }

    public void fillVBufferPerCell(VertexBuffer vb, PerCellFiller pcf)
    {
        int vc = getVertexCount();
        if (vb.getVcount() < vc) {
            for (int i = vc - vb.getVcount(); i != 0; --i) {
                vb.addVertex();
            }
        }

        Set<Integer> ready = new HashSet<>();
        for (int j = 0; j < cells.size(); j++) {
            Cell cell = cells.get(j);
            int[] corners = cell.getCorners();
            for (int i = 0; i < corners.length; i++) {
                if (ready.add(corners[i])) {
                    pcf.fill(vb.getVertex(corners[i]), j, i);
                }
            }
            cell.interpolate(vb);
        }
    }

    public int getTessfactor()
    {
        return tessfactor;
    }

    public Cell[] getCells()
    {
        Cell[] q = new Cell[cells.size()];
        return cells.toArray(q);
    }
}
