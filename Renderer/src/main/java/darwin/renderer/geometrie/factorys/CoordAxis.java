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
package darwin.renderer.geometrie.factorys;

import javax.media.opengl.GL;

import darwin.geometrie.data.DataLayout;
import darwin.geometrie.data.VertexBuffer;
import darwin.renderer.geometrie.packed.RenderMesh;
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

        darwin.geometrie.data.Element pos = new darwin.geometrie.data.Element(GLSLType.VEC3, "Position");

        DataLayout dl = new DataLayout(pos);

        VertexBuffer vb = new VertexBuffer(dl, 4);

        vb.newVertex().setAttribute(pos, 0f, 0f, 0f);
        vb.newVertex().setAttribute(pos, 1f, 0f, 0f);
        vb.newVertex().setAttribute(pos, 0f, 1f, 0f);
        vb.newVertex().setAttribute(pos, 0f, 0f, 1f);

        attr = new VertexBO(vb);
        indice = BufferObject.buildIndiceBuffer(1, 0, 2, 0, 3);
    }

    @Override
    public RenderMesh buildRenderable(Shader shader) {
        return new RenderMesh(shader, GL.GL_LINE_STRIP, indice, attr);
    }
}
