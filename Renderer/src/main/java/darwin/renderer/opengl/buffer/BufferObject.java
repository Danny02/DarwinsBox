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
package darwin.renderer.opengl.buffer;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.jogamp.opengl.util.GLBuffers;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import javax.media.opengl.GLProfile;

import darwin.renderer.GraphicContext;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public final class BufferObject
{

    static {
        assert GLProfile.isAvailable(GLProfile.GL2GL3) : "This device doesn't support BufferObjects";
    }

    public interface BufferFactory
    {

        public BufferObject create(Target target);

        public BufferObject buildIndiceBuffer(int... indice);
    }
    private final GraphicContext gc;
    public final int id;
    private final Target target;
    private int size;

    @AssistedInject
    public BufferObject(GraphicContext gcont, @Assisted Target target)
    {
        gc = gcont;
        this.target = target;
        int[] ids = new int[1];
        gc.getGL().glGenBuffers(1, ids, 0);
        id = ids[0];
    }

    @AssistedInject
    public BufferObject(GraphicContext gcont,
            @Assisted int... indice)
    {
        this(gcont, Target.ELEMENT_ARRAY);

        bind();
        {
            Buffer b = GLBuffers.newDirectIntBuffer(indice);
            bufferData(b, Type.STATIC, Usage.DRAW);
        }
        disable();
    }

    /**
     * Writes data to the buffer and allcoates as much memory as needed for the
     * data
     * <p/>
     * @param data  data which should be written to the buffer
     * @param usage Buffer usage type
     */
    public void bufferData(Buffer data, Type type, Usage usage)
    {
        int bsize = GLBuffers.sizeOfBufferElem(data);
        data.rewind();
        size = bsize * data.limit();
        gc.getGL().glBufferData(target.glvalue, size, data, getGLConst(type, usage));
    }

    /**
     * same usage as bufferData(...), but it allocates the memory with
     * <b>null</b> and uploads the data with bufferSubData. This is a common
     * driver optimization for frequent data uploading to the same buffer.
     * <p/>
     * @param data param type param usage
     */
    public void bufferDataOptimized(Buffer data, Type type, Usage usage)
    {
        allocate(data.limit() * GLBuffers.sizeOfBufferElem(data), type, usage);
        bufferSubData(0, data);
    }

    /**
     * allocate memory for the Buffer <br><br><i>glBufferData with null as
     * data</i></br></br>
     * <p/>
     * @param bsize memory size in bytes which should be allocated
     * @param usage Buffer usage type
     */
    public void allocate(int bsize, Type type, Usage usage)
    {
        size = bsize;
        gc.getGL().glBufferData(target.glvalue, size, null,
                getGLConst(type, usage));
    }

    /**
     * overwrites a specified data block, enough memory must already be
     * allocated in the buffer
     * <p/>
     * @param offset param data
     */
    public void bufferSubData(int offset, Buffer data)
    {
        int bsize = GLBuffers.sizeOfBufferElem(data);
        data.rewind();
        gc.getGL().glBufferSubData(target.glvalue, offset, bsize * data.limit(),
                data);
    }

    public ByteBuffer mapBuffer(Access access)
    {
        return gc.getGL().glMapBuffer(target.glvalue, access.glvalue);
    }

    public ByteBuffer mapRange(Access access, int offset, int length)
    {
        return gc.getGL().getGL2().glMapBufferRange(target.glvalue, offset, length,
                access.glvalue);
    }

    public void bind()
    {
        bind(id);
    }

    public void disable()
    {
        bind(0);
    }

    private void bind(int id)
    {
        gc.getGL().glBindBuffer(target.glvalue, id);
    }

    public int getSize()
    {
        return size;
    }

    public Target getTarget()
    {
        return target;
    }

    private static int getGLConst(Type type, Usage usage)
    {
        return type.glconst + usage.glEnumeration;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if(obj == this)
        	return true;
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BufferObject other = (BufferObject) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        return id;
    }
}
