/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.geometrie.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import javax.media.opengl.GL;

import darwin.geometrie.data.*;
import darwin.geometrie.unpacked.*;
import darwin.jopenctm.AttributeData;
import darwin.jopenctm.CtmFileReader;

import static darwin.geometrie.data.DataLayout.Format.*;
import static darwin.geometrie.data.DataType.*;

/**
 *
 * @author daniel
 */
public class CtmModelReader implements ModelReader
{

    private static final Element pos, tex, nor;

    static {
        pos = new Element(new GenericVector(FLOAT, 3), "position");
        tex = new Element(new GenericVector(FLOAT, 2), "texcoord");
        nor = new Element(new GenericVector(FLOAT, 3), "position");
    }

    @Override
    public ModelObjekt readModel(InputStream source) throws IOException, WrongFileTypeException
    {
        CtmFileReader cr = new CtmFileReader(source);
        darwin.jopenctm.Mesh rm = cr.getMesh();

        Collection<Element> elements = new ArrayList<>();
        elements.add(pos);

        if (rm.hasNormals()) {
            elements.add(nor);
        }

        Element[] uvEle = new Element[rm.getUVCount() - 1];
        if (rm.getUVCount() > 0) {
            elements.add(tex);
            if (rm.getUVCount() > 1) {
                VectorType float2 = new GenericVector(FLOAT, 2);
                for (int i = 1; i < rm.texcoordinates.length; i++) {
                    AttributeData ad = rm.texcoordinates[i];
                    uvEle[i - 1] = new Element(float2, ad.name);
                    elements.add(uvEle[i - 1]);
                }
            }
        }

        VectorType float4 = new GenericVector(FLOAT, 4);
        Element[] attEle = new Element[rm.getAttrCount()];
        for (int i = 0; i < rm.attributs.length; ++i) {
            attEle[i] = new Element(float4, rm.attributs[i].name);
            elements.add(attEle[i]);
        }

        Element[] elar = new Element[elements.size()];

        elements.toArray(elar);
        DataLayout layout = new DataLayout(AUTO, elar);
        int vcount = rm.getVertexCount();
        VertexBuffer vb = new VertexBuffer(layout, vcount);

        vb.fullyInitialize();

        fillElement(vb, pos, rm.vertices);

        if (rm.hasNormals()) {
            fillElement(vb, nor, rm.normals);
        }

        if (rm.getUVCount() > 0) {
            fillElement(vb, tex, rm.texcoordinates[0].values);
            if (rm.getUVCount() > 1) {
                VectorType float2 = new GenericVector(FLOAT, 2);
                for (int i = 1; i < rm.texcoordinates.length; i++) {
                    AttributeData ad = rm.texcoordinates[i];
                    elements.add(new Element(float2, ad.name));
                    fillElement(vb, uvEle[i - 1], rm.texcoordinates[i].values);
                }
            }
        }

        for (int i = 0; i < rm.attributs.length; ++i) {
            fillElement(vb, attEle[i], rm.attributs[i].values);
        }

        Mesh m = new Mesh(rm.indices, vb, GL.GL_TRIANGLES);

        //TODO Material
        return new ModelObjekt(new Model[]{new Model(m, null)});
    }

    public void fillElement(VertexBuffer vb, Element e, float[] data)
    {
        ByteBuffer b = ByteBuffer.allocateDirect(4 * data.length);
        FloatBuffer buffer = b.asFloatBuffer();
        buffer.put(data);
        buffer.rewind();

        for (Vertex v : vb) {
            v.setAttribute(e, b);
        }
    }

    @Override
    public boolean isSupported(String fileExtension)
    {
        return fileExtension.toLowerCase().equals("cmt");
    }
}
