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
package darwin.geometrie.io;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.zip.ZipInputStream;

import darwin.annotations.ServiceProvider;
import darwin.geometrie.data.*;
import darwin.geometrie.unpacked.*;
import darwin.jopenctm.data.AttributeData;
import darwin.jopenctm.errorhandling.*;
import darwin.jopenctm.io.CtmFileReader;

import javax.media.opengl.GL;

import static darwin.geometrie.data.DataLayout.Format.INTERLEAVE32;
import static darwin.geometrie.data.DataType.FLOAT;
import static darwin.jopenctm.data.Mesh.*;

/**
 *
 * @author daniel
 */
@ServiceProvider(ModelReader.class)
public class CtmModelReader implements ModelReader {

    public static final Element POSITION, TEX_COORD, NORMAL;

    static {
        POSITION = new Element(new GenericVector(FLOAT, CTM_POSITION_ELEMENT_COUNT), "Position");
        TEX_COORD = new Element(new GenericVector(FLOAT, CTM_UV_ELEMENT_COUNT), "TexCoord");
        NORMAL = new Element(new GenericVector(FLOAT, CTM_NORMAL_ELEMENT_COUNT), "Normal");
    }

    @Override
    public Model[] readModel(InputStream source) throws IOException, WrongFileTypeException {
        return new Model[]{readSingleModel(source)};
    }

    public Model[] readZipedModels(InputStream source) throws WrongFileTypeException, IOException {
        ZipInputStream zip = new ZipInputStream(source);

        List<Model> models = new ArrayList<>();
        while (zip.getNextEntry() != null) {
            models.add(readSingleModel(zip));
        }

        Model[] a = new Model[models.size()];
        models.toArray(a);
        return a;
    }

    public Model readSingleModel(InputStream source) throws WrongFileTypeException, IOException {
        CtmFileReader cr = new CtmFileReader(source);
        try {
            return convertMesh(cr.decode());
        } catch (BadFormatException ex) {
            throw new WrongFileTypeException("The model has some bad format: "
                                             + ex.getMessage());
        } catch (InvalidDataException ex) {
            throw new IOException("The model has some invalide data: " + ex.getMessage());
        }
    }

    @Override
    public boolean isSupported(String fileExtension) {
        return fileExtension.toLowerCase().equals("ctm");
    }

    public static Model convertMesh(darwin.jopenctm.data.Mesh mesh) {
        Collection<Element> elements = new ArrayList<>();
        elements.add(POSITION);

        if (mesh.hasNormals()) {
            elements.add(NORMAL);
        }

        Element[] uvEle = new Element[mesh.getUVCount()];
        if (mesh.getUVCount() > 0) {
            elements.add(TEX_COORD);
            if (mesh.getUVCount() > 1) {
                VectorType float2 = new GenericVector(FLOAT, 2);
                for (int i = 1; i < mesh.texcoordinates.length; i++) {
                    AttributeData ad = mesh.texcoordinates[i];
                    uvEle[i] = new Element(float2, ad.name);
                    elements.add(uvEle[i]);
                }
            }
        }

        VectorType float4 = new GenericVector(FLOAT, 4);
        Element[] attEle = new Element[mesh.getAttrCount()];
        for (int i = 0; i < mesh.attributs.length; ++i) {
            attEle[i] = new Element(float4, mesh.attributs[i].name);
            elements.add(attEle[i]);
        }

        Element[] elar = new Element[elements.size()];
        elements.toArray(elar);
        DataLayout layout = new DataLayout(INTERLEAVE32, elar);

        int vcount = mesh.getVertexCount();
        VertexBuffer vb = new VertexBuffer(layout, vcount);
        vb.fullyInitialize();

        fillElement(vb, POSITION, mesh.vertices);

        if (mesh.hasNormals()) {
            fillElement(vb, NORMAL, mesh.normals);
        }

        if (mesh.getUVCount() > 0) {
            fillElement(vb, TEX_COORD, mesh.texcoordinates[0].values);
            if (mesh.getUVCount() > 1) {
                VectorType float2 = new GenericVector(FLOAT, 2);
                for (int i = 1; i < mesh.texcoordinates.length; i++) {
                    AttributeData ad = mesh.texcoordinates[i];
                    elements.add(new Element(float2, ad.name));
                    fillElement(vb, uvEle[i], mesh.texcoordinates[i].values);
                }
            }
        }

        for (int i = 0; i < mesh.attributs.length; ++i) {
            fillElement(vb, attEle[i], mesh.attributs[i].values);
        }

        Mesh m = new Mesh(mesh.indices, vb, GL.GL_TRIANGLES);

        //TODO Material
        return new Model(m, null);
    }

    public static void fillElement(VertexBuffer vb, Element e, float[] data) {
//        ByteBuffer b = ByteBuffer.allocateDirect(4 * data.length);
//        FloatBuffer buffer = b.asFloatBuffer();
//        buffer.put(data);
//        buffer.rewind();

        int i = 0;
        for (Vertex v : vb) {
            v.setAttribute(e, data[i * 3], data[i * 3 + 1], data[i * 3 + 2]);
            i++;
        }
    }
}
