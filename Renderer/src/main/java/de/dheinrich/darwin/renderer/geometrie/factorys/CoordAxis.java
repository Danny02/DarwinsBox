/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.renderer.geometrie.factorys;

import de.dheinrich.darwin.renderer.opengl.BufferObject;
import de.dheinrich.darwin.renderer.opengl.BufferObject.Target;
import de.dheinrich.darwin.renderer.opengl.BufferObject.Usage;
import de.dheinrich.darwin.renderer.opengl.BufferObject.Type;
import de.dheinrich.darwin.renderer.geometrie.data.DataLayout;
import de.dheinrich.darwin.renderer.geometrie.data.DataLayout.Format;
import de.dheinrich.darwin.renderer.geometrie.data.RenderMesh;
import de.dheinrich.darwin.renderer.opengl.VertexBO;
import de.dheinrich.darwin.renderer.geometrie.data.Vertex;
import de.dheinrich.darwin.renderer.geometrie.data.VertexBuffer;
import de.dheinrich.darwin.renderer.opengl.Element;
import de.dheinrich.darwin.renderer.opengl.GLSLType;
import de.dheinrich.darwin.renderer.shader.Shader;
import java.nio.IntBuffer;
import javax.media.opengl.GL2GL3;

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

        DataLayout dl = new DataLayout(Format.INTERLEAVE, pos);

        VertexBuffer vb = new VertexBuffer(dl, 4);
        indice = new BufferObject(
                Target.ELEMENT_ARRAY);

        vb.newVertex().setAttribute(pos, 0f, 0f, 0f);
        vb.newVertex().setAttribute(pos, 1f, 0f, 0f);
        vb.newVertex().setAttribute(pos, 0f, 1f, 0f);
        vb.newVertex().setAttribute(pos, 0f, 0f, 1f);

        IntBuffer a = IntBuffer.allocate(5);
        int[] ind = new int[]{1, 0, 2, 0, 3};
        a.put(ind);
        indice.bind();
        {
            indice.bufferData(a, Type.STATIC, Usage.DRAW);
        }
        indice.disable();
        attr = new VertexBO(vb);
    }

    public RenderMesh buildRenderable(Shader shader) {
        return new RenderMesh(shader, GL2GL3.GL_LINE_STRIP, indice, attr);
    }
}
