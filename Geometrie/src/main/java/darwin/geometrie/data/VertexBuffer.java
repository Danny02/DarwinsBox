/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.geometrie.data;

import com.jogamp.opengl.util.GLBuffers;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Objects;

import static java.lang.Math.*;

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

    public VertexBuffer(DataLayout layout, int size)
    {
        this.size = size;
        this.layout = layout;
        buffer = GLBuffers.newDirectByteBuffer(size * layout.getBytesize());
        buffer.limit(0);
    }

    public void fullyInitialize()
    {
        setVCount(size);
    }

    public Vertex getVertex(int ind)
    {
        assert ind < getVcount() :
                "Vertexcount: " + getVcount() + ", requested Vertex: " + ind;
        return new Vertex(this, ind);
    }

    public int addVertex()
    {
        assert vcount < size;
        setVCount(++vcount);
        return vcount - 1;
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

    public void copyVertex(int id, VertexBuffer vbuffer)
    {
        copyVertex(id, 0, vbuffer);
    }

    public void copyVertex(int id, int offset, VertexBuffer vbuffer)
    {

        assert (id + offset < vcount) : "Das Ziel Vertex in das Kopiert werden"
                + " soll liegt außerhalb der Buffer Groesse.";
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

//    public void writeExternal(ObjectOutput out) throws IOException {
//        out.writeInt(vcount);
//        out.writeObject(layout);
//        buffer.position(0);
//        byte[] bytes = new byte[vcount * layout.getBytesize()];
//        buffer.get(bytes);
//        out.write(bytes);
//    }
//
//    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VertexBuffer other = (VertexBuffer) obj;
        if (!Objects.equals(this.layout, other.layout)) {
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

    public int hashCode()
    {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.buffer);
        hash = 97 * hash + Objects.hashCode(this.layout);
        hash = 97 * hash + this.size;
        hash = 97 * hash + this.vcount;
        return hash;
    }

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
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }
}
