/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer.geometrie.packed;

import javax.media.opengl.GL;

import static darwin.renderer.GraphicContext.*;

/**
 *
 * @author dheinrich
 */
public class TransparentWrapper implements TransparentRM
{
    private RenderModel model;

    public TransparentWrapper(RenderModel model) {
        this.model = model;
    }

    @Override
    public void render() {
        GL gl = getGL();
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        model.render();
        gl.glDisable(GL.GL_BLEND);
    }
}
