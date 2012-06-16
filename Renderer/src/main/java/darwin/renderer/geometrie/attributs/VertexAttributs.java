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

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import darwin.renderer.GraphicContext;
import darwin.renderer.geometrie.attributs.StdAttributs.StdAttributsFactory;
import darwin.renderer.geometrie.attributs.VAOAttributs.VAOAttributsFactory;
import darwin.renderer.opengl.VertexBO;
import darwin.renderer.opengl.buffer.BufferObject;
import darwin.renderer.shader.Shader;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
@Immutable
public class VertexAttributs
{
    public interface VAttributsFactory
    {
        @ParametersAreNonnullByDefault
        public VertexAttributs create(Shader shader, VertexBO[] vbuffers,
                                      @Nullable BufferObject indice);
    }
    private final GraphicContext gc;
    private final AttributsConfigurator configurator;
    private final int hash;

    @AssistedInject
    @ParametersAreNonnullByDefault
    public VertexAttributs(GraphicContext gcontext,
                           StdAttributsFactory stdFactory,
                           VAOAttributsFactory vaoFactory,
                           @Assisted Shader shader,
                           @Assisted VertexBO[] vbuffers,
                           @Assisted @Nullable BufferObject indice)
    {
        gc = gcontext;

        hash = shader.getAttributsHash();

        configurator = stdFactory.create(shader, vbuffers, indice);
        //TODO VAO switch
    }

    public void bind()
    {
        configurator.prepare();
    }

    public void disable()
    {
        configurator.disable();
    }

    public boolean isCompatible(Shader shader)
    {
        return hash == shader.getAttributsHash();
    }
}
