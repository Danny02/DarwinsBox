/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.renderer.geometrie.data;

import de.dheinrich.darwin.renderer.geometrie.attributs.*;
import de.dheinrich.darwin.renderer.opengl.*;
import de.dheinrich.darwin.renderer.shader.*;
import javax.media.opengl.*;

import static de.dheinrich.darwin.renderer.GraphicContext.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
//TODO Mesh organisation verbessern das verschiedenen shader genutzt werden können
public class RenderMesh implements Cloneable
{
    private VertexAttributs attributs;
    private final int indextype, vertexcount;
    private final BufferObject indice;
    private int primitivtype;
    private final boolean asarray;

    public RenderMesh(Shader shader, int primitivtype,
                      BufferObject indice, VertexBO... vertexdata) {
//        gl = (GL2GL3) GLProxy.newInstance(gl);
        this.primitivtype = primitivtype;
        asarray = indice == null;
        this.indice = indice;
        vertexcount = vertexdata[0].getVertexCount();
        attributs = new VertexAttributs(shader, vertexdata, indice);
        indextype = GL2GL3.GL_UNSIGNED_INT;
    }

    public RenderMesh(Shader shader, BufferObject indice, VertexBO... vertexdata) {
        this(shader, GL2GL3.GL_TRIANGLES, indice, vertexdata);
    }

    public int getIndexcount() {
        return asarray ? vertexcount : indice.getSize() / 4; // Integer 4 byte;
    }

    //TODO subsets auch erlauben, nicht nur genaue übereinstimmungen
    public boolean isCompatible(Shader shader) {
        return attributs.isCompatible(shader);
    }

    public void render() {
        renderRange(0, getIndexcount());
    }

    public void renderRange(int offset, int length) {
        attributs.bind();
        if (asarray)
            getGL().glDrawArrays(primitivtype, offset, length);
        else
            getGL().glDrawElements(primitivtype, length, indextype, offset * 4L);
        attributs.disable();
    }

    @Override
    public RenderMesh clone() {
        RenderMesh rm = null;
        try {
            rm =
            (RenderMesh) super.clone();
        } catch (CloneNotSupportedException ex) {
        }
        return rm;
    }
}
