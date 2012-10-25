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
package darwin.renderer.geometrie.packed;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import javax.annotation.concurrent.Immutable;
import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

import darwin.renderer.GraphicContext;
import darwin.renderer.geometrie.attributs.VertexAttributs;
import darwin.renderer.geometrie.attributs.VertexAttributs.VAttributsFactory;
import darwin.renderer.opengl.VertexBO;
import darwin.renderer.opengl.buffer.BufferObject;
import darwin.renderer.shader.Shader;

import javax.annotation.Nullable;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
//TODO Mesh organisation verbessern das verschiedenen shader genutzt werden können
@Immutable
public class RenderMesh implements Cloneable
{

    public interface RenderMeshFactory
    {

        public RenderMesh create(Shader shader, int primitivtype,
                BufferObject indice, VertexBO... vertexdata);

        public RenderMesh create(Shader shader, BufferObject indice, VertexBO... vertexdata);
    }
    private final GraphicContext gc;
    private final VertexAttributs attributs;
    private final int indextype, vertexcount;
    private final BufferObject indice;
    private final int primitivtype;
    private final boolean asarray;

    @AssistedInject
    public RenderMesh(GraphicContext gcontex,
            VAttributsFactory vfactory,
            @Assisted Shader shader,
            @Assisted int primitivtype,
            @Assisted @Nullable BufferObject indice,
            @Assisted VertexBO... vertexdata)
    {
        gc = gcontex;
        this.primitivtype = primitivtype;
        asarray = indice == null;
        this.indice = indice;
        vertexcount = vertexdata[0].getVertexCount();
        attributs = vfactory.create(shader, vertexdata, indice);
        indextype = GL2ES2.GL_UNSIGNED_INT;
    }

    @AssistedInject
    public RenderMesh(GraphicContext gcontex,
            VAttributsFactory vfactory,
            @Assisted Shader shader,
            @Assisted @Nullable BufferObject indice,
            @Assisted VertexBO... vertexdata)
    {
        this(gcontex, vfactory, shader, GL.GL_TRIANGLES, indice, vertexdata);
    }

    public int getIndexcount()
    {
        return asarray ? vertexcount : indice.getSize() / 4; // Integer 4 byte;
    }

    //TODO subsets auch erlauben, nicht nur genaue übereinstimmungen
    public boolean isCompatible(Shader shader)
    {
        return attributs.isCompatible(shader);
    }

    public void render()
    {
        renderRange(0, getIndexcount());
    }

    public void renderRange(int offset, int length)
    {
        attributs.bind();
        if (asarray) {
            gc.getGL().glDrawArrays(primitivtype, offset, length);
        } else {
            gc.getGL().glDrawElements(primitivtype, length, indextype, offset * 4L);
        }
        attributs.disable();
    }

    @Override
    public RenderMesh clone()
    {
        RenderMesh rm = null;
        try {
            rm =
                    (RenderMesh) super.clone();
        } catch (CloneNotSupportedException ex) {
        }
        return rm;
    }
}
