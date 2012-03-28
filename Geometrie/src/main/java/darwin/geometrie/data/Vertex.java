/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.geometrie.data;

import java.nio.ByteBuffer;


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
        assert (values.length <= ele.getVectorType().getElementCount()) :
                "The Attribute demands " + ele.getVectorType().getElementCount() + " values not " + values.length + "!";

        int id = ind * al.stride + al.offset;
        ele.getDataType().put(vb.buffer, id, values);
    }

    public void setAttribute(Element ele, ByteBuffer values)
    {
        DataAttribut al = vb.layout.getAttribut(ele);
        assert al != null : "This Attribute doesn't exist in the VertexBuffer";
        assert (values.remaining() >= ele.getVectorType().getByteSize()) :
                "not enough data in the buffer!";

        int old = vb.buffer.position();
        {
            int id = ind * al.stride + al.offset;
            vb.buffer.position(id * ele.getDataType().byteSize);
            int oldl = values.limit();
            {
                values.limit(values.position() + ele.getVectorType().getByteSize());
                vb.buffer.put(values);
            }
            values.limit(oldl);
        }
        vb.buffer.position(old);
    }

    public Number[] getAttribute(Element ele)
    {
        Number[] values = new Number[ele.getVectorType().getElementCount()];
        DataAttribut al = vb.layout.getAttribut(ele);

        int id = ind * al.stride + al.offset;
        ele.getDataType().get(vb.buffer, id, values);

        return values;
    }

    public void getAttribute(Element ele, ByteBuffer values)
    {
        DataAttribut al = vb.layout.getAttribut(ele);
        assert al != null : "This Attribute doesn't exist in the VertexBuffer";
        assert (values.remaining() >= ele.getVectorType().getByteSize()) :
                "not enough data in the buffer!";

        int old = vb.buffer.position();
        int oldl = vb.buffer.limit();
        {
            int id = ind * al.stride + al.offset;
            vb.buffer.position(id * ele.getDataType().byteSize);
            vb.buffer.limit(vb.buffer.position() + ele.getVectorType().getByteSize());
            values.put(vb.buffer);
        }
        vb.buffer.position(old);
        vb.buffer.limit(oldl);
    }
}
