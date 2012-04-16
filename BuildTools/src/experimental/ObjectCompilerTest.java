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
package experimental;

import de.theogonia.applet.renderer.geometrie.unpacked.ObjMaterial;
import de.theogonia.applet.util.io.obj.ObjFile;
import de.theogonia.applet.util.io.obj.ObjFileParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author some
 */
public class ObjectCompilerTest
{
    public ObjectCompilerTest() {
    }

    public void compile() {
        System.out.println("----- Compiling Object Files -----");
        File folder = new File("src/resources/Models");
        File[] files = folder.listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String name) {
                return name.endsWith("obj");
            }
        });

        long parse = 0, save = 0, total = 0, comp = 0;
        for (File obj : files) {
            long time = System.currentTimeMillis();
            ObjFileParser ofr =
                          new ObjFileParser("resources/Models/" + obj.getName());
            ObjFile f = ofr.loadOBJ();
            parse += System.currentTimeMillis() - time;
            try {
                int tris = 0;
                for(ObjMaterial m:f.getMaterials())
                    tris += f.getFaces(m).size();
                System.out.println(obj.getName()+" verts:"+f.getVerticies().size()+" faces:"+tris);
                
                FileOutputStream fos = new FileOutputStream(
                        obj.getAbsolutePath() + ".bin");
                ObjectOutputStream oos = new ObjectOutputStream(fos);

                time = System.currentTimeMillis();
                oos.writeObject(f);
                save += System.currentTimeMillis() - time;

                total += obj.length();
                comp += fos.getChannel().size();

                oos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("Objects Compressed: " + files.length);
        System.out.println("Parse time: " + parse
                + "ms;  Write/Compress time: " + save + "ms");
        System.out.println("Compression-Ratio: " + (float) comp / total);
        System.out.println();
    }

    public void load() {
        System.out.println("----- Load Compressed Object Files -----");
        File folder = new File("src/resources/Models");
        File[] files = folder.listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String name) {
                return name.endsWith("obj.bin");
            }
        });
        long load = 0;
        for (File obj : files)
            try {
                FileInputStream fos = new FileInputStream(
                        obj.getAbsolutePath());
                ObjectInputStream oos = new ObjectInputStream(fos);

                try {
                    long time = System.currentTimeMillis();
                    Object o = oos.readObject();
                    load += System.currentTimeMillis() - time;
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }

                oos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        System.out.println("Loading time: " + load + "ms");
        System.out.println();
    }

    public static void main(String[] args){
        new ObjectCompilerTest().compile();
    }
}
