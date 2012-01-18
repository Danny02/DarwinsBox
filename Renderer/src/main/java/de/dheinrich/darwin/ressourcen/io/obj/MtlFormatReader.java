/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under the terms of the GNU General Public License as published by  *  the Free Software Foundation, either version 3 of the License, or  *  (at your option) any later version.  *   *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.ressourcen.io.obj;

import de.dheinrich.darwin.renderer.geometrie.unpacked.ObjMaterial;
import de.dheinrich.darwin.ressourcen.resmanagment.RessourcesLoader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Hashtable;
import org.apache.log4j.Logger;

/**
 * Parser f�r das MTL Material Format
 * @author Daniel Heinrich
 */
public class MtlFormatReader {

    private static class Log {

        private static Logger ger = Logger.getLogger(MtlFormatReader.class);
    }
    private static final String modelfolder = "resources/Models/";
    private Hashtable<String, ObjMaterial> materials;
    private ObjMaterial accmat;
    private String path;

    public MtlFormatReader(String path) {
        this.path = path;
    }

    public Hashtable<String, ObjMaterial> loadMaterials() throws FileNotFoundException {

        if (materials == null) {
            materials = new Hashtable<String, ObjMaterial>();
            Reader fr;
            InputStream is = RessourcesLoader.getRessource(modelfolder + path);
            if (is == null) {
                Log.ger.error("Ressource \"" + path
                        + "\" konnte nicht gefunden werden.");
                throw new FileNotFoundException();
            }
            try {
                fr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(fr);
                String line;
                while ((line = br.readLine()) != null) {
                    String[] s = line.split(" ", 2);
                    if (s.length == 2) {
                        parseValue(s[0], s[1].trim().split(" "));
                    }
                }
                // <editor-fold defaultstate="collapsed" desc="catching stuff">
            } catch (IOException ex) {
                Log.ger.error("Ressource \"" + path
                        + "\" konnte nicht geladen werden.", ex);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                    }
                }
            }

        }
        return materials;
    }

    public void parseValue(String type, String[] values) {
        type = type.trim();

        if (type.equals("Ns")) {
            accmat.setSpecular_exponent(parseFloats(values)[0]);
        } else if (type.equals("Ka")) {
            accmat.setAmbient(parseFloats(values));
        } else if (type.equals("Kd")) {
            accmat.setDiffuse(parseFloats(values));
        } else if (type.equals("Ks")) {
            accmat.setSepcular(parseFloats(values));
        } else if (type.equals("Ke")) {
            accmat.setEmission(parseFloats(values));
        } else if (type.equals("map_Ka")) {
            accmat.setAmbientTex(mergestrings(values));
        } else if (type.equals("map_Kd")) {
            accmat.setDiffuseTex(mergestrings(values));
        } else if (type.equals("map_Ks")) {
            accmat.setSpecularTex(mergestrings(values));
        } else if (type.equals("map_bump")) {
            accmat.setNormalTex(mergestrings(values));
        } else if (type.equals("map_d")) {
            accmat.setAlphaTex(mergestrings(values));
        } else if (type.equals("bump")) {
            accmat.setBumbTex(values[0]);
        } else if (type.equals("newmtl")) {
            newMaterial(values[0]);
        }
    }

    private String mergestrings(String[] values) {
        StringBuilder s = new StringBuilder(values[0]);
        for (int i = 1; i < values.length; i++) {
            s.append(" ");
            s.append(values[i]);
        }
        return s.toString();
    }

    private void newMaterial(String name) {
        ObjMaterial mat;
        mat = materials.get(name);
        if (mat == null) {
            mat = new ObjMaterial(name);
            materials.put(name, mat);
        }
        accmat = mat;
    }

    private float[] parseFloats(String[] values) {
        float[] vals = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            if (!values[i].isEmpty()) {
                vals[i] = Float.parseFloat(values[i]);
            }
        }
        return vals;
    }
}
