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
package darwin.renderer.shader;

/*
 *
 * * Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com> * * This
 * program is free software: you can redistribute it and/or modify * it under
 * dheinrich.own.engineails. * * You should have received a copy of the GNU
 * General Public License * along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

import com.jogamp.opengl.util.texture.Texture;
import javax.media.opengl.GL;

import darwin.renderer.opengl.ShaderProgramm;

import static darwin.renderer.GraphicContext.*;

/**
 *
 * @author dheinrich
 */
public final class Sampler
{

    private boolean active;
    private int uniform_pos, texture_unit;
    private String uniname;

    public Sampler(String uniform, int texture_unit)
    {
        setActive(true);
        this.uniform_pos = -1;
        uniname = uniform;
        this.texture_unit = texture_unit;
    }

    protected void setActive(boolean active)
    {
        this.active = active;
    }

    public void bindTexture(Texture tex)
    {
        GL gl = getGL();
        gl.glActiveTexture(texture_unit);
        if (!isActive() || tex == null) {
            getGL().glBindTexture(GL.GL_TEXTURE_2D, 0);
        } else {
            tex.bind(gl);
        }
    }

    public boolean isActive()
    {
        return uniform_pos != -1 && active;
    }

    public void setShader(ShaderProgramm s)
    {
        uniform_pos = s.getUniformLocation(uniname);
        assert uniform_pos != -1;
        s.use();
        getGL().getGL2GL3().glUniform1i(uniform_pos, texture_unit - GL.GL_TEXTURE0);
    }
}
