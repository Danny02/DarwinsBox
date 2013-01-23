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
package darwin.renderer.opengl;

import darwin.geometrie.data.*;
import darwin.renderer.opengl.buffer.BufferObject.BufferFactory;
import darwin.renderer.opengl.buffer.*;

import com.google.inject.assistedinject.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
//TODO Sinn dieser Klasse ueberdenken
public class VertexBO
{
    public interface VBOFactoy
    {
        public VertexBO create(VertexBuffer vb);
    }
    public final DataLayout layout;
    public final BufferObject buffer;

    @AssistedInject
    public VertexBO(BufferFactory factory, @Assisted VertexBuffer vb) {
        buffer = factory.create(Target.ARRAY);
        buffer.bind();
        {
            buffer.bufferData(vb.buffer, Type.STATIC, Usage.DRAW);
        }
        buffer.disable();
        layout = vb.layout;
    }

    public VertexBO(DataLayout layout, BufferObject buffer) {
        assert buffer.getTarget() == Target.ARRAY;
        this.layout = layout;
        this.buffer = buffer;
    }

    public int getVertexCount() {
        return buffer.getSize() / layout.getBytesize();
    }
}
