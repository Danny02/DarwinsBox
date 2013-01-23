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

import java.nio.IntBuffer;
import java.util.*;
import com.jogamp.opengl.util.GLBuffers;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class SubGrid
{
    private final MapGrid grid;
    private final IntBuffer indice;
    private final Set<Integer> cells = new HashSet<>();

    public SubGrid(MapGrid grid) {
        this.grid = grid;
        indice = GLBuffers.newDirectIntBuffer(grid.getIndexCount());

    }

    public void addQuad(int x, int y) {
        int h = grid.getWidth()*y +x;
        if (cells.add(h)) {
            Cell q = grid.getCell(x, y);
            indice.put(q.getTriangles());
        }
    }

    public IntBuffer getIndice() {
        return indice;
    }
}
