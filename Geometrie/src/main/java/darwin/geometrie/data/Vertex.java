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
package darwin.geometrie.data;

import java.nio.BufferOverflowException;
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
            vb.buffer.position(id);
            int oldLim = values.limit();
            {
                int newLim = values.position() + ele.getVectorType().getByteSize();
                if (newLim > oldLim) {
                    throw new BufferOverflowException();
                }
                values.limit(newLim);
                vb.buffer.put(values);
            }
            values.limit(oldLim);
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
