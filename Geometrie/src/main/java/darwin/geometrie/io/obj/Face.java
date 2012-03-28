/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under the terms of the GNU General Public License as published by  *  the Free Software Foundation, either version 3 of the License, or  *  (at your option) any later version.  *   *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.geometrie.io.obj;

import java.io.*;
import java.util.Arrays;

/**
 *
 * @author Daniel Heinrich
 */
public class Face implements Externalizable
{
    private VertexIDs[] vertice;

    public Face() {
    }

    public Face(VertexIDs... vertice) {
        this.vertice = vertice;
    }

    public VertexIDs[] getVertice() {
        return vertice;
    }

    public int getVertCount() {
        return vertice.length;
    }

    public int getTriCount() {
        return 1 + getVertCount() - 3;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Face other = (Face) obj;
        if (!Arrays.deepEquals(this.vertice, other.vertice))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + Arrays.deepHashCode(this.vertice);
        return hash;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(vertice.length);
        for (VertexIDs id : vertice) {
            out.writeInt(id.position);
            out.writeInt(id.texcoord);
            out.writeInt(id.normal);
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        byte len = in.readByte();
        vertice = new VertexIDs[len];
        for (int i = 0; i < len; ++i)
            vertice[i] = new VertexIDs(in.readInt(), in.readInt(), in.readInt());
    }
}
