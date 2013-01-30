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

import darwin.geometrie.data.*;
import darwin.renderer.geometrie.packed.RenderMesh;
import darwin.renderer.geometrie.packed.RenderMesh.RenderMeshFactory;
import darwin.renderer.opengl.VertexBO;
import darwin.renderer.opengl.VertexBO.VBOFactoy;
import darwin.renderer.opengl.buffer.BufferObject;
import darwin.renderer.opengl.buffer.BufferObject.BufferFactory;
import darwin.renderer.shader.Shader;

import com.google.inject.assistedinject.*;

import static darwin.geometrie.data.DataLayout.Format.INTERLEAVE;
import static darwin.renderer.opengl.GLSLType.*;
import static darwin.geometrie.io.ModelReader.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class Rahmen implements GeometryFactory
{

    public interface RahmenFactory
    {

        public Rahmen create(float inset);
    }
    private final VertexBO vbo;
    private final BufferObject indice;
    private final RenderMesh.RenderMeshFactory factory;

    @AssistedInject
    public Rahmen(RenderMeshFactory rmFactory, VBOFactoy vFactoy,
            BufferFactory bFactory, @Assisted float inset)
    {
        factory = rmFactory;
        Element pos = new Element(VEC2, POSITION_ATTRIBUTE);
        Element alpha = new Element(FLOAT, "Alpha");
        DataLayout dl = new DataLayout(INTERLEAVE, pos, alpha);

        float posIns = 1f - inset;
        float negIns = -1f + inset;
        vbo = vFactoy.create(new VertexBuffer(dl,
                -1, -1, 1,
                -1, 1, 1,
                1, 1, 1,
                1, -1, 1,
                negIns, negIns, 0,
                negIns, posIns, 0,
                posIns, posIns, 0,
                posIns, negIns, 0));

        indice = bFactory.buildIndiceBuffer(
                0, 4, 1,
                4, 5, 1,//left
                5, 2, 1,
                5, 6, 2,//top
                6, 3, 2,
                6, 7, 3,//right
                7, 0, 3,
                7, 4, 0);//bottom
    }

    @Override
    public RenderMesh buildRenderable(Shader shader)
    {
        return factory.create(shader, indice, vbo);
    }
}
