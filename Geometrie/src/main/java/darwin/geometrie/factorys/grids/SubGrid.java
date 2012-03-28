/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.geometrie.factorys.grids;

import com.jogamp.opengl.util.GLBuffers;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

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
