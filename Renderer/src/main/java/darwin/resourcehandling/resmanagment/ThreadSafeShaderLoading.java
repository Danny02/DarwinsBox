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
package darwin.resourcehandling.resmanagment;

import darwin.renderer.opengl.ShaderProgramm;
import darwin.renderer.shader.Shader;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLRunnable;


/**
 *
 * @author dheinrich
 */
public class ThreadSafeShaderLoading implements GLRunnable{
    private Shader sc;
    private ShaderProgramm sp;

    public ThreadSafeShaderLoading(Shader sc, ShaderProgramm sp) {
        this.sc = sc;
        this.sp = sp;
    }

    @Override
    public boolean run(GLAutoDrawable glad) {
        sc.ini(sp);
        return true;
    }

}
