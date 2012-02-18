/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
