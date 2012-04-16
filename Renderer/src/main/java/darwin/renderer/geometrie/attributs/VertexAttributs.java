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

import darwin.renderer.opengl.BufferObject;
import darwin.renderer.opengl.VertexBO;
import darwin.renderer.shader.Shader;

import static darwin.renderer.GraphicContext.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class VertexAttributs
{

    private static class Static
    {

        private static final boolean vao_available = checkExtension();
    }
    private final AttributsConfigurator configurator;
    private final int hash;

    public VertexAttributs(Shader shader, VertexBO[] vbuffers,
            BufferObject indice)
    {
        hash = shader.getAttributsHash();

        //TODO indice werden im TerrainRenderer mit VAO nicht richtig gebunden
        if (Static.vao_available) {
            configurator = new VAOAttributs(shader, vbuffers, indice);
        } else {
            configurator = new StdAttributs(shader, vbuffers, indice);
        }
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

    private static boolean checkExtension()
    {
        return getGL().isExtensionAvailable("GL_ARB_vertex_array_object");
    }
}
