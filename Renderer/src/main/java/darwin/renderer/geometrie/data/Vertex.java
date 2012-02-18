/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer.geometrie.data;

import java.nio.ByteBuffer;

import darwin.renderer.opengl.Element;

/**
 * Proxy Klasse um ein einzelens Vertex aus einem VertexBuffer editieren zu
 * k�nnen
 * <p/>
 * @author Daniel Heinrich
 */
public final class Vertex
{

    private final VertexBuffer vb;
    public final int ind;

    Vertex(VertexBuffer vb, int ind)
    {
        this.vb = vb;
        this.ind = ind;
    }

    public void setAttribute(Element ele, Number... values)
    {
        DataAttribut al = vb.layout.getAttribut(ele);
        assert al != null : "This Attribute doesn't exist in the VertexBuffer";
        assert (values.length <= ele.gltype.size) :
                "The Attribute demands " + ele.gltype.size + " values not " + values.length + "!";

        int id = ind * al.stride + al.offset;
        ele.gltype.datatype.put(vb.buffer, id, values);
    }

    public void setAttribute(Element ele, ByteBuffer values)
    {
        DataAttribut al = vb.layout.getAttribut(ele);
        assert al != null : "This Attribute doesn't exist in the VertexBuffer";
        assert (values.remaining() >= ele.gltype.getByteSize()) :
                "not enough data in the buffer!";

        int old = vb.buffer.position();
        {
            int id = ind * al.stride + al.offset;
            vb.buffer.position(id * ele.gltype.datatype.byteSize);
            int oldl = values.limit();
            {
                values.limit(values.position() + ele.gltype.getByteSize());
                vb.buffer.put(values);
            }
            values.limit(oldl);
        }
        vb.buffer.position(old);
    }

    public Number[] getAttribute(Element ele)
    {
        Number[] values = new Number[ele.gltype.size];
        DataAttribut al = vb.layout.getAttribut(ele);

        int id = ind * al.stride + al.offset;
        ele.gltype.datatype.get(vb.buffer, id, values);

        return values;
    }

    public void getAttribute(Element ele, ByteBuffer values)
    {
        DataAttribut al = vb.layout.getAttribut(ele);
        assert al != null : "This Attribute doesn't exist in the VertexBuffer";
        assert (values.remaining() >= ele.gltype.getByteSize()) :
                "not enough data in the buffer!";

        int old = vb.buffer.position();
        int oldl = vb.buffer.limit();
        {
            int id = ind * al.stride + al.offset;
            vb.buffer.position(id * ele.gltype.datatype.byteSize);
            vb.buffer.limit(vb.buffer.position() + ele.gltype.getByteSize());
            values.put(vb.buffer);
        }
        vb.buffer.position(old);
        vb.buffer.limit(oldl);
    }
}
