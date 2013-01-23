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
package darwin.renderer.geometrie.attributs;

import darwin.renderer.GraphicContext;
import darwin.renderer.opengl.VertexBO;
import darwin.renderer.opengl.buffer.BufferObject;
import darwin.renderer.shader.Shader;

import com.google.inject.assistedinject.*;
import javax.media.opengl.GLProfile;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public final class VAOAttributs implements AttributsConfigurator {

    public interface VAOAttributsFactory {

        public VAOAttributs create(Shader shader, VertexBO[] vbuffers,
                                   BufferObject indice);
    }

    static {
        assert GLProfile.isAvailable(GLProfile.GL2GL3) : "This device VertexAttributsdoesn't support VAOs";
    }
    private final GraphicContext gc;
    private final int id;

    @AssistedInject
    public VAOAttributs(GraphicContext gcontext,
                        @Assisted Shader shader,
                        @Assisted VertexBO[] vbuffers,
                        @Assisted  BufferObject indice) {
        gc = gcontext;

        int[] i = new int[1];
        gc.getGL().getGL2GL3().glGenVertexArrays(1, i, 0);
        id = i[0];

        StdAttributs sa = new StdAttributs(gc, shader, vbuffers, indice);
        prepare();
        sa.prepare();
        disable();
        //TODO: wtf??? GL Error about vbo not bound
        sa.disable();
    }

    @Override
    public void prepare() {
        gc.getGL().getGL2GL3().glBindVertexArray(id);
    }

    @Override
    public void disable() {
        gc.getGL().getGL2GL3().glBindVertexArray(0);
    }
}
