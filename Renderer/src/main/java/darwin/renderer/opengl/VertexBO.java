/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer.opengl;

import darwin.geometrie.data.DataLayout;
import darwin.geometrie.data.VertexBuffer;
import darwin.renderer.opengl.BufferObject.Target;
import darwin.renderer.opengl.BufferObject.Type;
import darwin.renderer.opengl.BufferObject.Usage;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
//TODO Sinn dieser Klasse ueberdenken
public class VertexBO
{
    public final DataLayout layout;
    public final BufferObject buffer;

    public VertexBO(VertexBuffer vb) {
        buffer = new BufferObject(Target.ARRAY);
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
