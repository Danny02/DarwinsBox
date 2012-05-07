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
