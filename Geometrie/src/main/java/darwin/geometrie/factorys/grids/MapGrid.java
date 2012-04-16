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

import darwin.geometrie.data.DataLayout.Format;
import darwin.geometrie.data.*;
import darwin.geometrie.factorys.PerCellFiller;
import darwin.geometrie.factorys.PerVertexFiller;
import darwin.util.math.base.Matrix4;
import darwin.util.math.base.Vector;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class MapGrid
{

    private final int width, height;
    private final Cell[] cells;
    private final VertexBuffer vertice;  // Vertexold Data
    private final int count;

    public MapGrid(int w, int h, int tessfactor)
    {
        assert tessfactor % 2 == 0 : "muss gerade sein weil mittelpunkt notwendig";

        width = w;
        height = h;

        CellFactory factory = new CellFactory(tessfactor);

        int tmp = height * width;
        Vector[] cellpos = new Vector[tmp + (height - 1) * (width - 1)];
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                int x = i + j;
                int y = -i + j;
                cellpos[j + i * height] = new Vector(x, y);
            }
        }

        for (int i = 0; i < height - 1; ++i) {
            for (int j = 0; j < width - 1; ++j) {
                int x = i + j + 1;
                int y = -i + j;
                cellpos[tmp + j + i * (height - 1)] = new Vector(x, y);
            }
        }

        count = factory.createCells(cellpos);

        cells = factory.getCells();

        VectorType float2 = new GenericVector(DataType.FLOAT, 2);
        final Element pos = new Element(float2, "Position");
        final Element tex = new Element(float2, "TexCoord");
        final Element tex2 = new Element(float2, "TexCoordLocal");

        vertice = new VertexBuffer(new DataLayout(Format.AUTO, pos, tex, tex2),
                factory.getVertexCount());

        final float w2 = (float) Math.sqrt(2);
        final Matrix4 m = new Matrix4();
        m.loadIdentity();
        double scale = 1. / ((width - 1) * w2);
        m.scale(scale, scale, 0);
        m.translate(0, (height - 0.5) * w2, 0);
        m.rotateEuler(0, 0, -45);

        factory.fillVBufferPerVertex(vertice, new PerVertexFiller()
        {

            @Override
            public void fill(Vertex vertex, Vector position)
            {
                double[] p = position.getCoords();
                vertex.setAttribute(pos, (float) p[0], (float) p[1]);

                Vector t = m.mult(position);
                vertex.setAttribute(tex, (float) t.getCoords()[0],
                        (float) t.getCoords()[1]);
            }
        });


        final float[][] right = new float[][]{{0, 0}, {1, 0}, {1, 1}, {0, 1}};
        final float[][] left = new float[][]{{1, 1}, {0, 1}, {0, 0}, {1, 0}};
        factory.fillVBufferPerCell(vertice, new PerCellFiller()
        {

            @Override
            public void fill(Vertex vertex, int cellid, int corner)
            {
                int w = cellid % width;
                int h = cellid / width;

                float[] uv;
                if (h % 2 == w % 2) {
                    uv = right[corner];
                } else {
                    uv = left[corner];
                }
                vertex.setAttribute(tex2, uv[0], uv[1]);
            }
        });
    }

    public int[] getIndicies()
    {
        int[] res = new int[getIndexCount()];
        int idcount = cells[0].getTriCount() * 3;
        for (int i = 0; i < cells.length; ++i) {
            int[] tris = cells[i].getTriangles();
            System.arraycopy(tris, 0, res, idcount * i, tris.length);
        }
        return res;
    }

    public int getCornerCount()
    {
        return count;
    }

    public int getIndexCount()
    {
        return getCellIndexCount() * cells.length;
    }

    public int getCellIndexCount()
    {
        return cells[0].getTriCount() * 3;
    }

    public int getCellOffset(int x, int y)
    {
        return getCellID(x, y) * getCellIndexCount();
    }

    public VertexBuffer getVertices()
    {
        return vertice;
    }

    public int getVertexCount()
    {
        return vertice.getVcount();
    }

    public Cell getCellByID(int id)
    {
        assert id > 0 && id < cells.length;
        return cells[id];
    }

    public Cell getCell(int x, int y)
    {
        return cells[getCellID(x, y)];
    }

    private int getCellID(int x, int y)
    {
        assert y < getHeight();
        assert y >= 0 && x >= 0 : "x&y must be positiv(" + x + "," + y + ')';

        int offset;
        if (y % 2 == 0) {
            assert x < width : "x:" + x + " grid width:" + width;
            offset = y / 2 * width;
        } else {
            assert x < width - 1 : "x:" + y + " grid 2nd width:" + (width - 1);
            offset = width * height + y / 2 * (width - 1);
        }

        return offset + x;
    }

    public int getCellCount()
    {
        return cells.length;
    }

    public int getHeight()
    {
        return height + height - 1;
    }

    public int getWidth()
    {
        return width;
    }
}
