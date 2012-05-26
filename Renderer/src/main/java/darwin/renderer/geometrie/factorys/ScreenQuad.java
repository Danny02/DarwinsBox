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

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.media.opengl.GL;

import darwin.geometrie.data.Element;
import darwin.geometrie.data.VertexBuffer;
import darwin.renderer.geometrie.packed.RenderMesh;
import darwin.renderer.geometrie.packed.RenderMesh.RenderMeshFactory;
import darwin.renderer.opengl.VertexBO;
import darwin.renderer.opengl.VertexBO.VBOFactoy;
import darwin.renderer.shader.Shader;

import static darwin.renderer.opengl.GLSLType.*;

/**
 * Initialisiert ein einzelnes Quad das als Screen Quad genutzt werden kann
 * <p/>
 * @author Daniel Heinrich
 */
@Immutable
@Singleton
public class ScreenQuad implements GeometryFactory
{

    private final VertexBO vbo;
    private final RenderMeshFactory factory;

    @Inject
    @ParametersAreNonnullByDefault
    public ScreenQuad(RenderMeshFactory rmFactory, VBOFactoy factoy)
    {
        factory = rmFactory;
        vbo = factoy.create(new VertexBuffer(new Element(VEC2, "Position"),
                -1, -1,
                1, -1,
                -1, 1,
                1, 1));
    }

    @Override
    public RenderMesh buildRenderable(Shader shader)
    {
        return factory.create(shader, GL.GL_TRIANGLE_STRIP, null, vbo);
    }
}
