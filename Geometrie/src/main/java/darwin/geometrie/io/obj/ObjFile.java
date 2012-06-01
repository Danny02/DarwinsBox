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
package darwin.geometrie.io.obj;

import java.util.*;

import darwin.util.math.base.vector.*;

/**
 * DatenModell einer OBJ Datei
 * <p/>
 * @author Daniel Heinrich
 */
public class ObjFile
{
    private List<Vector3> verticies = new ArrayList<>();
    private List<Vector3> normals = new ArrayList<>();
    private List<Vector2> texcoords = new ArrayList<>();
    transient private Vector3 min = new Vector3(), max = new Vector3();
    private Map<ObjMaterial, List<Face>> subobjekts = new HashMap<>();
    transient private List<Face> accfaces;

    public void addVertex(ImmutableVector<Vector3> pos)
    {
        min.min(pos);
        max.max(pos);

        verticies.add(pos.copy());
    }

    public void addFace(Face face)
    {
        if (accfaces == null) {
            accfaces = new LinkedList<>();
            subobjekts.put(new ObjMaterial("__empty"), accfaces);
        }

        accfaces.add(face);
    }

    public void setAccMaterial(ObjMaterial mat)
    {
        accfaces = subobjekts.get(mat);
        if (accfaces == null) {
            accfaces = new LinkedList<>();
            subobjekts.put(mat, accfaces);
        }
    }

    public void center()
    {
        if (verticies.isEmpty()) {
            return;
        }

        Vector3 shift = min.copy().add(max).mul(-0.5f);

        for (Vector3 v : verticies) {
            v.add(shift);
        }

        min.add(shift);
        max.add(shift);
    }

    public void rescale(float scale)
    {
        if (scale == 1) {
            return;
        }

        for (Vector3 v : verticies) {
            v.mul(scale);
        }


        max.mul(scale);
        min.mul(scale);
    }

    public List<Face> getFaces(ObjMaterial mat)
    {
        return subobjekts.get(mat);
    }

    public Set<ObjMaterial> getMaterials()
    {
        return subobjekts.keySet();
    }

    public List<Vector3> getNormals()
    {
        return normals;
    }

    public List<Vector2> getTexcoords()
    {
        return texcoords;
    }

    public List<Vector3> getVerticies()
    {
        return verticies;
    }

    public double getWidth()
    {
        return max.getX() - min.getX();
    }

    public double getHeight()
    {
        return max.getY() - min.getY();
    }

    public double getDepth()
    {
        return max.getZ() - min.getZ();
    }

    public Vector3 getMin()
    {
        return min;
    }

    public Vector3 getMax()
    {
        return max;
    }

    public boolean hasNormals()
    {
        return !normals.isEmpty();
    }

    public boolean hasTexCoords()
    {
        return !texcoords.isEmpty();
    }

    public boolean checkIntegrity(boolean attributeIntegrity)
    {
        for (List<Face> subs : subobjekts.values()) {
            for (Face f : subs) {
                for (VertexIDs vid : f.getVertice()) {
                    if (attributeIntegrity && hasNormals()) {
                        if (vid.normal == 0) {
                            return false;
                        }
                    }
                    if (attributeIntegrity && hasTexCoords()) {
                        if (vid.texcoord == 0) {
                            return false;
                        }
                    }
                    if (!inRange(vid.normal, normals.size())) {
                        return false;
                    }
                    if (!inRange(vid.position, verticies.size())) {
                        return false;
                    }
                    if (!inRange(vid.texcoord, texcoords.size())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean inRange(int index, int max)
    {
        if (index < 0) {
            index = max + index + 1;
        }
        return index <= max;
    }
}
