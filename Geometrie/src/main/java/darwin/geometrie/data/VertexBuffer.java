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

import com.jogamp.opengl.util.GLBuffers;
import java.nio.ByteBuffer;
import java.util.*;

import static darwin.geometrie.data.DataLayout.Format.INTERLEAVE;
import static java.lang.Math.max;

/**
 * DatenModel um Vertex Attribute eines Modeles zu halten
 * <p/>
 * @author Daniel Heinrich
 */
public final class VertexBuffer implements Iterable<Vertex>
{

    public final ByteBuffer buffer;
    public final DataLayout layout;
    private int size;
    private int vcount = 0;

    public VertexBuffer(Element e, int size)
    {
        this(new DataLayout(INTERLEAVE, e), size);
    }

    public VertexBuffer(DataLayout layout, int size)
    {
        this.size = size;
        this.layout = layout;
        buffer = GLBuffers.newDirectByteBuffer(size * layout.getBytesize());
        buffer.limit(0);
    }

    public VertexBuffer(Element e, float... data)
    {
        this(new DataLayout(INTERLEAVE, e), data);
    }

    public VertexBuffer(DataLayout layout, float... data)
    {
        this(layout, data.length / (layout.getBytesize() / 4));

        for (Element e : layout.getElements()) {
            if (e.getDataType() != DataType.FLOAT) {
                throw new IllegalArgumentException("The layout must consist of only float tpye data elements!");
            }
        }
        if (data.length % (layout.getBytesize() / 4) != 0) {
            throw new IllegalArgumentException("The data size is not an excate multiple of the data layout size!");
        }

        setVCount(size);
        buffer.asFloatBuffer().put(data);
    }

    public void fullyInitialize()
    {
        setVCount(size);
    }

    public Vertex getVertex(int ind)
    {
        if (ind >= getVcount()) {
            throw new IndexOutOfBoundsException("Vertexcount: " + getVcount()
                    + ", requested Vertex: " + ind);
        }
        return new Vertex(this, ind);
    }

    public int addVertex()
    {
        assert (vcount < size) : "No new vertices can be added to this vertex buffer!";
        int vid = vcount;
        setVCount(vcount + 1);
        return vid;
    }

    public Vertex newVertex()
    {
        return getVertex(addVertex());
    }

    private void setVCount(int vc)
    {
        vcount = vc;
        buffer.limit(vcount * layout.getBytesize());
    }

    public void copyInto(int offset, VertexBuffer vbuffer)
    {
        assert (size >= vbuffer.vcount + offset) :
                "Destination Buffer not large enough";
        assert (offset <= vcount) :
                "Offset:" + offset + " vcount:" + vcount + "  (offset>vcount)";

        setVCount(vbuffer.vcount + offset);
        if (!vbuffer.layout.equals(layout)) {
            setVCount(max(vcount, vbuffer.vcount + offset));
            for (int i = 0; i < vbuffer.vcount; ++i) {
                copyVertex(i, offset, vbuffer);
            }
        } else {
            buffer.position(layout.getBytesize() * offset);
            ByteBuffer buf = vbuffer.buffer;
            buf.position(0);
            buffer.put(buf);
        }
    }

    public void copyVertex(int id, int offset, VertexBuffer vbuffer)
    {
        assert (id + offset < vcount) : "The destination vertex is not in bound of the destination buffer";
        if (layout.equals(vbuffer.layout)) {
            ByteBuffer buf = vbuffer.buffer;
            int limit = offset + layout.getBytesize();
            assert !(limit <= buf.capacity() || id > vcount) :
                    "Couldn't copy Vertex";

            buf.position(offset);
            buf.limit(limit);
            buffer.position(layout.getBytesize() * id);
            buffer.put(buf);
        } else {
            Vertex src = vbuffer.getVertex(id);
            Vertex dst = getVertex(id + offset);
            for (Element e : layout.getElements()) {
                if (vbuffer.layout.hasElement(e)) {
                    dst.setAttribute(e, src.getAttribute(e));
                }
            }
        }
    }

    public int getSize()
    {
        return size;
    }

    public int getVcount()
    {
        return vcount;
    }

    public int remaining()
    {
        return getSize() - getVcount();
    }

    /**
     * The remove method of the Iterator Interface is not supported and will
     * throw an UnsupportedOperationException
     */
    @Override
    public Iterator<Vertex> iterator()
    {

        final VertexBuffer vb = this;
        return new Iterator<Vertex>()
        {

            int i = 0;

            @Override
            public boolean hasNext()
            {
                return i < vcount;
            }

            @Override
            public Vertex next()
            {
                return vb.getVertex(i++);
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VertexBuffer other = (VertexBuffer) obj;
        if (this.buffer != other.buffer && (this.buffer == null || !this.buffer.equals(other.buffer))) {
            return false;
        }
        if (this.layout != other.layout && (this.layout == null || !this.layout.equals(other.layout))) {
            return false;
        }
        if (this.size != other.size) {
            return false;
        }
        if (this.vcount != other.vcount) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.buffer);
        hash = 53 * hash + Objects.hashCode(this.layout);
        hash = 53 * hash + this.size;
        hash = 53 * hash + this.vcount;
        return hash;
    }
}
