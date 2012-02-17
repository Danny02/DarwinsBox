/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package darwin.renderer.geometrie.factorys;

import java.util.Arrays;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class Quad {
    private final int[] vertice;

    public Quad(int v1, int v2, int v3, int v4) {
        this.vertice = new int[]{v1,v2,v3,v4};
    }

    public int getVertice(int id){
        assert id < 4;
        return vertice[id];
    }

    public int[] getTriangles(){
        int[] ret = new int[6];
        ret[2] = ret[4] = vertice[0];
        ret[1] = vertice[1];
        ret[0] = ret[3] = vertice[2];
        ret[5] = vertice[3];

        return ret;
    }

    public void copyTriangles(int[] indice, int offset){
        System.arraycopy(vertice, 0, indice, offset, 4);
        indice[4+offset] = vertice[0];
        indice[5+offset] = vertice[2];
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Quad other = (Quad) obj;
        if (!Arrays.equals(this.vertice, other.vertice))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + vertice[0];
        hash = 13 * hash + vertice[1];
        hash = 13 * hash + vertice[2];
        hash = 13 * hash + vertice[3];
        return hash;
    }

}
