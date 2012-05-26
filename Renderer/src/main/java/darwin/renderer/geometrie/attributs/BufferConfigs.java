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

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLProfile;

import darwin.renderer.GraphicContext;
import darwin.renderer.opengl.buffer.BufferObject;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
@Immutable
class BufferConfigs
{

    static {
        assert GLProfile.isAvailable(GLProfile.GL2ES2) : "This device doesn't support Generic Vertex Attributes";
    }
    private final GraphicContext gc;
    private final BufferObject buffer;
    private final AttributConfig[] configs;

    @ParametersAreNonnullByDefault
    public BufferConfigs(GraphicContext gcontext, BufferObject buffer,
            AttributConfig[] configs)
    {
        gc = gcontext;
        this.buffer = buffer;
        this.configs = configs;
    }

    public void prepare()
    {
        buffer.bind();
        GL2ES2 gl = gc.getGL().getGL2ES2();
        for (AttributConfig ac : configs) {
            gl.glEnableVertexAttribArray(ac.index);
            gl.glVertexAttribPointer(ac.index, ac.size, ac.glconst,
                    false, ac.stride, ac.offset);
        }
        buffer.disable();
    }

    public void disable()
    {
        GL2ES2 gl = gc.getGL().getGL2ES2();
        for (AttributConfig ac : configs) {
            gl.glDisableVertexAttribArray(ac.index);
        }
    }
}
