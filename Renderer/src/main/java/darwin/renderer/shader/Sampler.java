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
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.jogamp.opengl.util.texture.Texture;
import javax.media.opengl.GL;

import darwin.renderer.GraphicContext;
import darwin.renderer.opengl.ShaderProgramm;

/**
 *
 * @author dheinrich
 */
public final class Sampler {

    public interface SamplerFactory {

        public Sampler create(String uniform, int textureUnit);
    }
    private final GraphicContext gc;
    private final int textureUnit;
    private final String uniname;
    private boolean active;
    private int uniform_pos;

    @AssistedInject
    public Sampler(GraphicContext gcont,
                   @Assisted String uniform, @Assisted int textureUInit) {
        gc = gcont;
        this.textureUnit = textureUInit;
        uniname = uniform;

        setActive(true);
        uniform_pos = -1;
    }

    protected void setActive(boolean active) {
        this.active = active;
    }

    public int getTextureUnit() {
        return textureUnit;
    }

    public void bindTexture(Texture tex) {
        GL gl = gc.getGL();
        gl.glActiveTexture(textureUnit);
        if (!isActive() || tex == null) {
            gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
        } else {
            tex.bind(gl);
        }
    }

    public boolean isActive() {
        return uniform_pos != -1 && active;
    }

    public void setShader(ShaderProgramm s) {
        uniform_pos = s.getUniformLocation(uniname);
        s.use();
        gc.getGL().getGL2GL3().glUniform1i(uniform_pos, textureUnit - GL.GL_TEXTURE0);
    }
}
