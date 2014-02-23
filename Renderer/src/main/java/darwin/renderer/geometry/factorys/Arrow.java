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
package darwin.renderer.geometry.factorys;

import darwin.geometrie.data.*;
import darwin.renderer.geometrie.packed.RenderMesh.RenderMeshFactory;
import darwin.renderer.opengl.VertexBO.VBOFactoy;

import javax.inject.*;
import javax.media.opengl.GL2ES2;

import static darwin.renderer.opengl.GLSLType.VEC3;
import static darwin.geometrie.io.ModelReader.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
@Singleton
public final class Arrow implements GeometryFactory
{

    private final VertexBO vbo;
    private final RenderMeshFactory factory;

    @Inject
    public Arrow(RenderMeshFactory rmFactory, VBOFactoy factoy)
    {
        factory = rmFactory;
        vbo = factoy.create(new VertexBuffer(new Element(VEC3, POSITION_ATTRIBUTE),
                0, 0, 0,
                1, 1, 0,
                1, .75f, 0,
                1, 1, 0,
                .75f, 1, 0));
    }

    @Override
    public RenderMesh buildRenderable(Shader shader)
    {
        return factory.create(shader, GL2ES2.GL_LINE_STRIP, null, vbo);
    }
}
