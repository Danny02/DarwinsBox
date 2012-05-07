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

import darwin.geometrie.data.DataLayout.Format;
import darwin.geometrie.data.*;
import darwin.renderer.geometrie.packed.RenderMesh;
import darwin.renderer.opengl.GLSLType;
import darwin.renderer.opengl.VertexBO;
import darwin.renderer.shader.Shader;

/**
 * Initialisiert ein einzelnes Quad das als Screen Quad genutzt werden kann
 * <p/>
 * @author Daniel Heinrich
 */
public class ScreenQuad implements GeometryFactory
{

    private static class Static
    {

        public static GeometryFactory instance = new ScreenQuad();
    }

    public static GeometryFactory getInstance()
    {
        return Static.instance;
    }
    private VertexBO attr;

    private ScreenQuad()
    {
        Element pos = new Element(GLSLType.VEC2, "Position");
        VertexBuffer vb =
                new VertexBuffer(new DataLayout(Format.INTERLEAVE, pos), 4);
        vb.newVertex().setAttribute(pos, -1f, -1f);
        vb.newVertex().setAttribute(pos, 1f, -1f);
        vb.newVertex().setAttribute(pos, -1f, 1f);
        vb.newVertex().setAttribute(pos, 1f, 1f);

        attr = new VertexBO(vb);
    }

    @Override
    public RenderMesh buildRenderable(Shader shader)
    {
        RenderMesh rm = new RenderMesh(shader, GL.GL_TRIANGLE_STRIP, null,
                attr);
        return rm;
    }
}
