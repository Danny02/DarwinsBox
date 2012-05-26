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
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import darwin.geometrie.data.DataLayout;
import darwin.geometrie.data.Element;
import darwin.renderer.GraphicContext;
import darwin.renderer.opengl.VertexBO;
import darwin.renderer.opengl.buffer.BufferObject;
import darwin.renderer.shader.Shader;
import darwin.renderer.shader.ShaderAttribute;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
@Immutable
public class StdAttributs implements AttributsConfigurator
{

    private final BufferConfigs[] configs;
    private final BufferObject indice;

    @AssistedInject
    @ParametersAreNonnullByDefault
    public StdAttributs(GraphicContext gcontext,
            @Assisted Shader shader,
            @Assisted VertexBO[] vbuffers,
            @Assisted @Nullable BufferObject indice)
    {
        assert shader.isInitialized() : "Shader is not initialized!";

        this.indice = indice;

        configs = new BufferConfigs[vbuffers.length];

        for (int i = 0; i < vbuffers.length; ++i) {
            DataLayout dl = vbuffers[i].layout;
            List<AttributConfig> cs = new LinkedList<>();
            for (Element ele : dl.getElements()) {
                ShaderAttribute sa = shader.getAttribut(ele);
                if (sa != null) {
                    cs.add(new AttributConfig(sa, dl.getAttribut(ele)));
                }
            }
            AttributConfig[] c = new AttributConfig[cs.size()];
            cs.toArray(c);
            configs[i] = new BufferConfigs(gcontext, vbuffers[i].buffer, c);
        }
    }

    @Override
    public void prepare()
    {
        for (BufferConfigs bc : configs) {
            bc.prepare();
        }
        if (indice != null) {
            indice.bind();
        }
    }

    @Override
    public void disable()
    {
        for (BufferConfigs bc : configs) {
            bc.disable();
        }
        if (indice != null) {
            indice.disable();
        }
    }
}
