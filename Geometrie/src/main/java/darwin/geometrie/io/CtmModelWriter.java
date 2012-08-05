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

import darwin.annotations.ServiceProvider;
import darwin.geometrie.data.*;
import darwin.geometrie.io.obj.ObjModelReader;
import darwin.geometrie.unpacked.Mesh;
import darwin.geometrie.unpacked.Model;
import darwin.jopenctm.compression.*;
import darwin.jopenctm.data.AttributeData;
import darwin.jopenctm.errorhandling.InvalidDataException;
import darwin.jopenctm.io.CtmFileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.*;
import javax.media.opengl.GL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static darwin.geometrie.data.DataType.FLOAT;
import static darwin.jopenctm.data.Mesh.*;

/**
 *
 * @author daniel
 */
@ServiceProvider(ModelWriter.class)
public class CtmModelWriter implements ModelWriter {

    public static final String FILE_EXTENSION = "ctm";
    private final static String DEFAULT_COMMENT = "Exported with Darwin Lib";
    private static final Element POSITION, TEX_COORD, NORMAL;

    static {
        POSITION = new Element(new GenericVector(FLOAT, CTM_POSITION_ELEMENT_COUNT), "Position");
        TEX_COORD = new Element(new GenericVector(FLOAT, CTM_UV_ELEMENT_COUNT), "TexCoord");
        NORMAL = new Element(new GenericVector(FLOAT, CTM_NORMAL_ELEMENT_COUNT), "Normal");
    }
    private static final Logger logger = LoggerFactory.getLogger(CtmModelWriter.class);
    private final MeshEncoder encoder;
    private final String fileComment;
    private final int compressionLevel;

    //<editor-fold defaultstate="collapsed" desc="Builder">
    public static class Builder {

        private MeshEncoder encoder = new RawEncoder();
        private String fileComment = DEFAULT_COMMENT;
        private int compressionLevel = 5;

        private Builder() {
        }

        public Builder andCompressionLevel(int cl) {
            compressionLevel = cl;
            return this;
        }

        public Builder andEncoder(MeshEncoder enc) {
            encoder = enc;
            return this;
        }

        public Builder andComment(String c) {
            fileComment = c;
            return this;
        }

        public CtmModelWriter build() {
            return new CtmModelWriter(encoder, fileComment, compressionLevel);
        }
    }

    public static Builder withCompressionLevel(int cl) {
        return new Builder().andCompressionLevel(cl);
    }

    public static Builder withEncoder(MeshEncoder enc) {
        return new Builder().andEncoder(enc);
    }

    public static Builder withComment(String c) {
        return new Builder().andComment(c);
    }
    //</editor-fold>

    public CtmModelWriter() {
        this(new RawEncoder(), DEFAULT_COMMENT, 5);
    }

    public CtmModelWriter(MeshEncoder encoder, String fileComment, int compressionLevel) {
        if (encoder == null) {
            throw new NullPointerException("The Encoder musn't be null!");
        }
        this.encoder = encoder;
        this.fileComment = fileComment;
        this.compressionLevel = compressionLevel;
    }

    @Override
    public String getDefaultFileExtension() {
        return FILE_EXTENSION;
    }

    //TODO da ohnehin ein Zip Ordner besteht könnte man hier auch alle weiteren Informationen ablegen
    //material, animation usw.
    @Override
    public void writeModel(OutputStream out, Model[] models) throws IOException {
        ZipOutputStream zip = new ZipOutputStream(out);
        for (int i = 0; i < models.length; i++) {
            zip.putNextEntry(new ZipEntry("model" + i + ".ctm"));

            writeSingleModel(zip, models[i]);

            zip.closeEntry();
        }
        zip.finish();
    }

    public void writeSingleModel(OutputStream out, Model m) throws IOException {
        CtmFileWriter writer = new CtmFileWriter(out, encoder, compressionLevel);

        String matName = null;
        if (m.getMat() != null) {
            matName = m.getMat().name;
        }
        try {
            writer.encode(convertMesh(m.getMesh(), matName), fileComment);
        } catch (InvalidDataException ex) {
            throw new IOException("The model has some invalid data: " + ex.getMessage());
        }
    }

    private static darwin.jopenctm.data.Mesh convertMesh(Mesh mesh, String matName) throws IOException {
        //standard checks
        VertexBuffer vbuffer = mesh.getVertices();
        int vc = mesh.getVertexCount();
        if (!vbuffer.layout.hasElement(POSITION)) {
            throw new IOException("The mesh doesn't have a float3 vertex position attribute!");
        }

        if (mesh.getPrimitiv_typ() != GL.GL_TRIANGLES) {
            throw new IOException("The CTM File Format only supports triangle Meshes");
        }

        //create indicie array
        int[] meshIndicies = mesh.getIndicies();
        if (meshIndicies == null) {
            throw new IOException("Only meshes with indices can be exported!");
        }
        int[] indices = new int[mesh.getIndexCount()];
        System.arraycopy(meshIndicies, 0, indices, 0, indices.length);


        AttributeData[] atts = createAttributeData(vbuffer);

        //create position array
        float[] vertices = new float[vc * CTM_POSITION_ELEMENT_COUNT];
        int i = 0;
        for (Vertex v : vbuffer) {
            copyToBuffer(vertices, i, v, POSITION);
            i += CTM_POSITION_ELEMENT_COUNT;
        }

        //create optional normal array
        float[] normals = null;
        if (vbuffer.layout.hasElement(NORMAL)) {
            normals = new float[vc * CTM_NORMAL_ELEMENT_COUNT];
            int k = 0;
            for (Vertex v : vbuffer) {
                copyToBuffer(normals, k, v, NORMAL);
                k += CTM_NORMAL_ELEMENT_COUNT;
            }
        }

        //create uv arrays
        boolean hasUV = vbuffer.layout.hasElement(TEX_COORD);
        AttributeData[] texcoords = new AttributeData[hasUV ? 1 : 0];
        if (hasUV) {
            float[] values = new float[vc * CTM_UV_ELEMENT_COUNT];
            int k = 0;
            for (Vertex v : vbuffer) {
                copyToBuffer(values, k, v, TEX_COORD);
                k += CTM_UV_ELEMENT_COUNT;
            }
            texcoords[0] = new AttributeData("TexCoord", matName,
                                             AttributeData.STANDARD_UV_PRECISION, values);
        }

        return new darwin.jopenctm.data.Mesh(vertices, normals, indices,
                                             texcoords, atts);
    }

    private static void copyToBuffer(float[] buffer, int offset, Vertex v, Element e) {
        Number[] data = v.getAttribute(e);
        for (int j = 0; j < data.length; j++) {
            buffer[offset + j] = (Float) data[j];
        }
        //System.arraycopy(data, 0, buffer, offset, data.length);
    }

    private static AttributeData[] createAttributeData(VertexBuffer vbuffer) {
        List<AttributeData> attribute = new ArrayList<>();
        for (Element el : vbuffer.layout.getElements()) {
            if (el.equals(POSITION) || el.equals(NORMAL) || el.equals(TEX_COORD)) {
                continue;
            }

            if (el.getDataType() != FLOAT || el.getVectorType().getElementCount() > CTM_ATTR_ELEMENT_COUNT) {
                logger.warn("The mesh-attribute " + el.toString()
                            + " can't be exported to the ctm format! Only float "
                            + "attributes with max. 4 elements are supported.");
                continue;
            }

            float[] values = new float[vbuffer.getVcount() * CTM_ATTR_ELEMENT_COUNT];

            {
                int k = 0;
                for (Vertex v : vbuffer) {
                    copyToBuffer(values, k, v, el);
                    k += CTM_ATTR_ELEMENT_COUNT;
                }
            }

            attribute.add(new AttributeData(el.getBezeichnung(), null,
                                            AttributeData.STANDARD_PRECISION, values));
        }

        AttributeData[] atts = new AttributeData[attribute.size()];
        attribute.toArray(atts);

        return atts;
    }
}
