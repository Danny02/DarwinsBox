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

import javax.inject.Inject;
import javax.media.opengl.GL;

import darwin.geometrie.data.Element;
import darwin.geometrie.data.VertexBuffer;
import darwin.renderer.geometrie.packed.RenderMesh;
import darwin.renderer.geometrie.packed.RenderMesh.RenderMeshFactory;
import darwin.renderer.opengl.VertexBO;
import darwin.renderer.opengl.VertexBO.VBOFactoy;
import darwin.renderer.opengl.buffer.BufferObject;
import darwin.renderer.opengl.buffer.BufferObject.BufferFactory;
import darwin.renderer.shader.Shader;

import static darwin.renderer.opengl.GLSLType.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class CoordAxis implements GeometryFactory
{

    private final RenderMesh.RenderMeshFactory factory;
    private final VertexBO vbo;
    private final BufferObject indice;

    @Inject
    public CoordAxis(RenderMeshFactory rmFactory, VBOFactoy vFactoy, BufferFactory bFactory)
    {
        factory = rmFactory;
        vbo = vFactoy.create(new VertexBuffer(new Element(VEC3, "Position"),
                0, 0, 0,
                1, 0, 0,
                0, 1, 0,
                0, 0, 1));
        indice = bFactory.buildIndiceBuffer(1, 0, 2, 0, 3);
    }

    @Override
    public RenderMesh buildRenderable(Shader shader)
    {
        return factory.create(shader, GL.GL_LINE_STRIP, indice, vbo);
    }
}
