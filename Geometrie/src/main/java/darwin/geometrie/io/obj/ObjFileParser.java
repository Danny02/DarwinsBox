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

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

import darwin.util.math.base.Vec3;
import darwin.util.math.base.Vector;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class ObjFileParser
{

    private static class Log
    {

        private static Logger ger = Logger.getLogger(ObjFileParser.class);
    }
    private Pattern leer, slash;
    private Map<String, ObjMaterial> materials;

    public ObjFileParser()
    {
        materials = new HashMap<>();
        leer = Pattern.compile(" ");
        slash = Pattern.compile("/");
    }

    public synchronized ObjFile loadOBJ(InputStream in) throws IOException
    {
        ObjFile obj = new ObjFile();
        Reader fr = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null) {
            String[] s = leer.split(line, 2);
            if (s.length == 2) {
                parseValue(obj, s[0], leer.split(s[1].trim()));
            }
        }

        return obj;
    }

    private void parseValue(ObjFile obj, String type, String[] values) throws IOException
    {
        switch (type) {
            case "v":
                obj.addVertex(new Vec3(parseDoubless(values)));
                break;
            case "vn":
                obj.getNormals().add(new Vec3(parseDoubless(values)));
                break;
            case "vt":
                double[] vals = parseDoubless(values);
                if (vals.length != 2) {
                    throw new IOException("The Texture Coordinats of the OBJ file must be of only 2 elements!");
                }
                obj.getTexcoords().add(new Vector(vals));
                break;
            case "f":
                obj.addFace(parseFace(values));
                break;
            case "usemtl":
                useMaterial(obj, values);
                break;
            case "mtllib":
                parseMtlLib(values);
                break;
        }
    }

    private double[] parseDoubless(String[] values) throws IOException
    {
        double[] vals = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            if (!values[i].isEmpty()) {
                try {
                    vals[i] = Double.parseDouble(values[i]);
                } catch (NumberFormatException ex) {
                    throw new IOException("Exception while trying to parse nummber values!", ex);
                }
            }
        }
        return vals;
    }

    private Face parseFace(String[] values) throws IOException
    {
        VertexIDs[] vertice = new VertexIDs[values.length];

        for (int i = 0; i < vertice.length; i++) {
            String[] ids = slash.split(values[i]);
            int[] vertex = new int[3];
            for (int j = 0; j < ids.length; j++) {
                if (!ids[j].isEmpty()) {
                    vertex[j] = Integer.parseInt(ids[j]);
                    if (vertex[j] == 0) {
                        throw new IOException("OBJ indicies mustn't be 0!");
                    }
                } else {
                    vertex[j] = 0;
                }
            }
            vertice[i] = new VertexIDs(vertex);
        }

        return new Face(vertice);
    }

    private void parseMtlLib(String[] mtllib)
    {
        try {
            MtlFormatReader mtl = new MtlFormatReader(mtllib[0]);
            materials = mtl.loadMaterials();
        } catch (IOException ex) {
            Log.ger.error("Eine Material Bibilothek konnte nicht geladen werde.\n\t"
                    + ex.getLocalizedMessage());
            //TODO import womoeglich abbrechen
        }
    }

    private void useMaterial(ObjFile obj, String[] values)
    {
        String name = values[0];
        ObjMaterial mat = materials.get(name);
        if (mat == null) {
            mat = new ObjMaterial(name);
            materials.put(name, mat);
        }

        obj.setAccMaterial(mat);
    }
}
