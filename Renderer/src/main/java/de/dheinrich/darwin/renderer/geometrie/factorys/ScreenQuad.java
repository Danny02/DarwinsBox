/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.renderer.geometrie.factorys;

import de.dheinrich.darwin.renderer.geometrie.data.DataLayout;
import de.dheinrich.darwin.renderer.geometrie.data.DataLayout.Format;
import de.dheinrich.darwin.renderer.geometrie.data.RenderMesh;
import de.dheinrich.darwin.renderer.opengl.VertexBO;
import de.dheinrich.darwin.renderer.geometrie.data.VertexBuffer;
import de.dheinrich.darwin.renderer.opengl.Element;
import de.dheinrich.darwin.renderer.opengl.GLSLType;
import de.dheinrich.darwin.renderer.shader.Shader;
import javax.media.opengl.GL2GL3;

/**
 * Initialisiert ein einzelnes Quad das als Screen Quad genutzt werden kann
 * @author Daniel Heinrich
 */
public class ScreenQuad implements GeometryFactory
{
    private static class Static
    {
        public static GeometryFactory instance = new ScreenQuad();
    }

    public static GeometryFactory getInstance() {
        return Static.instance;
    }
    private VertexBO attr;

    private ScreenQuad() {
        Element pos = new Element(GLSLType.VEC2, "Position");
        VertexBuffer vb =
                     new VertexBuffer(new DataLayout(Format.INTERLEAVE, pos), 4);
        vb.newVertex().setAttribute(pos, -1f, -1f);
        vb.newVertex().setAttribute(pos, 1f, -1f);
        vb.newVertex().setAttribute(pos, -1f, 1f);
        vb.newVertex().setAttribute(pos, 1f, 1f);

        attr = new VertexBO(vb);
    }

    public RenderMesh buildRenderable(Shader shader) {
        RenderMesh rm = new RenderMesh(shader, GL2GL3.GL_TRIANGLE_STRIP, null,
                                       attr);
        return rm;
    }
}
