/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.dheinrich.darwin.renderer.geometrie.factorys;

import de.dheinrich.darwin.renderer.geometrie.data.DataLayout.Format;
import de.dheinrich.darwin.renderer.geometrie.data.*;
import de.dheinrich.darwin.renderer.opengl.*;
import de.dheinrich.darwin.renderer.shader.*;
import javax.media.opengl.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public final class Arrow implements GeometryFactory{

    public static final GeometryFactory instance = new Arrow();
    private final VertexBO attr;

    private Arrow() {
        Element pos = new Element(GLSLType.VEC3, "Position");

        DataLayout dl = new DataLayout(Format.INTERLEAVE, pos);

        VertexBuffer vb = new VertexBuffer(dl, 5);

        vb.newVertex().setAttribute(pos, 0f, 0f, 0f);
        vb.newVertex().setAttribute(pos, 1f, 1f, 0f);
        vb.newVertex().setAttribute(pos, 1f, .75f, 0f);
        vb.newVertex().setAttribute(pos, 1f, 1f, 0f);
        vb.newVertex().setAttribute(pos, .75f, 1f, 0f);

        attr = new VertexBO(vb);
    }

    public RenderMesh buildRenderable(Shader shader) {
        return new RenderMesh(shader, GL2GL3.GL_LINE_STRIP, null, attr);
    }
}
