package darwin.renderer.shader;

/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.jogamp.opengl.util.texture.*;
import darwin.renderer.opengl.*;
import javax.media.opengl.*;

import static darwin.renderer.GraphicContext.*;

/**
 *
 * @author dheinrich
 */
public class Sampler
{
    private boolean active;
    private int uniform_pos, texture_unit;
    private String uniname;

    public Sampler(String uniform, int texture_unit) {
        setActive(true);
        this.uniform_pos = -1;
        uniname = uniform;
        this.texture_unit = texture_unit;
    }

    protected void setActive(boolean active) {
        this.active = active;
    }

    public void bindTexture(Texture tex) {
        GL gl = getGL();
        gl.glActiveTexture(texture_unit);
        if (!isActive() || tex == null) {
            //TODO not everytime Texture_2D
            getGL().glBindTexture(GL.GL_TEXTURE_2D, 0);
            return;
        }
        tex.bind(gl);
    }

    public boolean isActive() {
        return uniform_pos != -1 && active;
    }

    public void setShader(ShaderProgramm s) {
        uniform_pos = s.getUniformLocation(uniname);
//        assert uniform_pos != -1;
        s.use();
        getGL2GL3().glUniform1i(uniform_pos, texture_unit - GL.GL_TEXTURE0);
    }
}
