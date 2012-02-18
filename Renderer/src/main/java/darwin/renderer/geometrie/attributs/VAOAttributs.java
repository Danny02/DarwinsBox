/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer.geometrie.attributs;

import javax.media.opengl.GLProfile;

import darwin.renderer.opengl.BufferObject;
import darwin.renderer.opengl.VertexBO;
import darwin.renderer.shader.Shader;

import static darwin.renderer.GraphicContext.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
final class VAOAttributs implements AttributsConfigurator
{

    static {
        assert GLProfile.isAvailable(GLProfile.GL2GL3) : "This device doesn't support VAOs";
    }
    private final int id;

    public VAOAttributs(Shader shader, VertexBO[] vbuffers, BufferObject indice)
    {

        int[] i = new int[1];
        getGL().getGL2GL3().glGenVertexArrays(1, i, 0);
        id = i[0];

        StdAttributs sa = new StdAttributs(shader, vbuffers, indice);
        prepare();
        sa.prepare();
        disable();
        //TODO: wtf??? GL Error about vbo not bound
        sa.disable();
    }

    @Override
    public void prepare()
    {
        getGL().getGL2GL3().glBindVertexArray(id);
    }

    @Override
    public void disable()
    {
        getGL().getGL2GL3().glBindVertexArray(0);
    }
}
