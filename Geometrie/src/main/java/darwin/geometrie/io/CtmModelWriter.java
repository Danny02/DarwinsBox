/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.geometrie.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL;
import org.apache.log4j.Logger;

import darwin.annotations.ServiceProvider;
import darwin.geometrie.data.*;
import darwin.geometrie.unpacked.Mesh;
import darwin.geometrie.unpacked.Model;
import darwin.jopenctm.compression.MG1Encoder;
import darwin.jopenctm.compression.MeshEncoder;
import darwin.jopenctm.data.AttributeData;
import darwin.jopenctm.errorhandling.InvalidDataException;
import darwin.jopenctm.io.CtmFileWriter;

import static darwin.geometrie.data.DataType.*;
import static darwin.jopenctm.data.Mesh.*;

/**
 *
 * @author daniel
 */
@ServiceProvider(ModelWriter.class)
public class CtmModelWriter implements ModelWriter
{

    public static final Logger logger = Logger.getLogger(CtmModelWriter.class);
    public static final String FILE_EXTENSION = "ctm";
    private final static String DEFAULT_COMMENT = "Exported with Darwin Lib";
    private static final Element position, texcoord, normal;

    static {
        position = new Element(new GenericVector(FLOAT, CTM_POSITION_ELEMENT_COUNT), "Position");
        texcoord = new Element(new GenericVector(FLOAT, CTM_UV_ELEMENT_COUNT), "TexCoord");
        normal = new Element(new GenericVector(FLOAT, CTM_NORMAL_ELEMENT_COUNT), "Normal");
    }
    private final MeshEncoder encoder;
    private final String fileComment;

    public CtmModelWriter()
    {
        this(new MG1Encoder());
    }

    public CtmModelWriter(MeshEncoder encoder)
    {
        this(encoder, DEFAULT_COMMENT);
    }

    public CtmModelWriter(MeshEncoder encoder, String fileComment)
    {
        if (encoder == null) {
            throw new NullPointerException("The Encoder musn't be null!");
        }
        this.encoder = encoder;
        this.fileComment = fileComment;
    }

    @Override
    public void writeModel(OutputStream out, Model[] models) throws IOException
    {
        CtmFileWriter writer = new CtmFileWriter(out, encoder);
        for (Model m : models) {
            try {
                writer.encode(convertMesh(m.getMesh(), m.getMat().name), fileComment);
            } catch (InvalidDataException ex) {
                throw new IOException("The model has some invalid data: " + ex.getMessage());
            }
        }
    }

    private darwin.jopenctm.data.Mesh convertMesh(Mesh mesh, String matName) throws IOException
    {
        if (mesh.getPrimitiv_typ() != GL.GL_TRIANGLES) {
            throw new IOException("The CTM File Format only supports triangle Meshes");
        }

        VertexBuffer vbuffer = mesh.getVertices();
        int vc = mesh.getVertexCount();

        float[] vertices = new float[vc * CTM_POSITION_ELEMENT_COUNT];
        float[] normals = null;
        AttributeData texcoords = null;
        int[] indices = new int[mesh.getIndexCount()];

        if (!vbuffer.layout.hasElement(position)) {
            throw new IOException("The mesh doesn't have a float3 vertex position attribute!");
        }

        List<AttributeData> attribute = new ArrayList<>();
        for (Element el : vbuffer.layout.getElements()) {
            if (el.equals(position) || el.equals(normal) || el.equals(texcoord)) {
                continue;
            }
            if (el.getDataType() != FLOAT || el.getVectorType().getElementCount() > 4) {
                logger.warn("The mesh-attribute " + el.toString() + " can't be exported to the ctm format!");
                continue;
            }
            float[] values = new float[vc * CTM_ATTR_ELEMENT_COUNT];
            int k = 0;
            for (Vertex v : vbuffer) {
                copyToBuffer(values, k, v, el);
                k += CTM_ATTR_ELEMENT_COUNT;
            }
            AttributeData adata = new AttributeData(el.getBezeichnung(), null,
                    AttributeData.STANDART_PRECISION, values);
        }

        System.arraycopy(mesh.getIndicies(), 0, indices, 0, indices.length);

        int i = 0;
        for (Vertex v : vbuffer) {
            copyToBuffer(vertices, i, v, position);
            i += CTM_POSITION_ELEMENT_COUNT;
        }

        if (vbuffer.layout.hasElement(normal)) {
            normals = new float[vc * CTM_NORMAL_ELEMENT_COUNT];
            int k = 0;
            for (Vertex v : vbuffer) {
                copyToBuffer(normals, k, v, normal);
                k += CTM_NORMAL_ELEMENT_COUNT;
            }
        }
        if (vbuffer.layout.hasElement(texcoord)) {
            float[] values = new float[vc * CTM_UV_ELEMENT_COUNT];
            int k = 0;
            for (Vertex v : vbuffer) {
                copyToBuffer(values, k, v, texcoord);
                k += CTM_UV_ELEMENT_COUNT;
            }
            texcoords = new AttributeData("TexCoord", matName,
                    AttributeData.STANDART_UV_PRECISION, values);
        }

        AttributeData[] atts = new AttributeData[attribute.size()];
        attribute.toArray(atts);
        return new darwin.jopenctm.data.Mesh(vertices, normals, indices,
                texcoords == null ? new AttributeData[0] : new AttributeData[]{texcoords},
                atts);
    }

    private void copyToBuffer(float[] buffer, int offset, Vertex v, Element e)
    {
        Number[] data = v.getAttribute(e);
        for (int j = 0; j < data.length; j++) {
            buffer[offset + j] = (float) data[j];
        }
    }

    @Override
    public String getDefaultFileExtension()
    {
        return FILE_EXTENSION;
    }
}
