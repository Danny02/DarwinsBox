/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer.geometrie.factorys;

import java.nio.IntBuffer;
import javax.media.opengl.GL;

import darwin.renderer.geometrie.data.DataLayout.Format;
import darwin.renderer.geometrie.data.*;
import darwin.renderer.opengl.BufferObject.Target;
import darwin.renderer.opengl.BufferObject.Type;
import darwin.renderer.opengl.BufferObject.Usage;
import darwin.renderer.opengl.*;
import darwin.renderer.shader.Shader;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class CoordAxis implements GeometryFactory
{
    public static final GeometryFactory instance = new CoordAxis();
    private final VertexBO attr;
    private final BufferObject indice;

    private CoordAxis() {
        Element pos = new Element(GLSLType.VEC3, "Position");

        DataLayout dl = new DataLayout(pos);

        VertexBuffer vb = new VertexBuffer(dl, 4);

        vb.newVertex().setAttribute(pos, 0f, 0f, 0f);
        vb.newVertex().setAttribute(pos, 1f, 0f, 0f);
        vb.newVertex().setAttribute(pos, 0f, 1f, 0f);
        vb.newVertex().setAttribute(pos, 0f, 0f, 1f);

        attr = new VertexBO(vb);
        indice = BufferObject.buildIndiceBuffer(1, 0, 2, 0, 3);
    }

    public RenderMesh buildRenderable(Shader shader) {
        return new RenderMesh(shader, GL.GL_LINE_STRIP, indice, attr);
    }
}
