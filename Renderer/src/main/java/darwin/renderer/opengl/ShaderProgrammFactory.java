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
package darwin.renderer.opengl;

import java.util.List;
import javax.inject.Inject;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;

import darwin.renderer.shader.BuildException;
import darwin.renderer.shader.ShaderAttribute;

import static darwin.renderer.shader.BuildException.BuildError.*;

/**
 *
 * @author daniel
 */
public class ShaderProgrammFactory
{
    private final GLAutoDrawable drawable;

    @Inject
    public ShaderProgrammFactory(GLAutoDrawable drawable)
    {
        this.drawable = drawable;
    }

    public ShaderProgramm create(List<ShaderAttribute> attr, ShaderObjekt... sobject) throws BuildException
    {
        GL2ES2 gl = drawable.getGL().getGL2ES2();
        int programObject = gl.glCreateProgram();

        for (ShaderObjekt so : sobject) {
            if (so == null) {
                continue;
            }
            gl.glAttachShader(programObject, so.getShaderobjekt());
        }

        //TODO GL Treiber Constanten in einen Globalen Constanten-Pool verfrachten
        int[] max = new int[1];
        gl.glGetIntegerv(GL2ES2.GL_MAX_VERTEX_ATTRIBS, max, 0);

        //TODO buggy, anscheined werden manche attribute ned gefunden
        for (ShaderAttribute sa : attr) {
            int index = sa.getIndex();
            if (index >= 0 && index < max[0]) {
                gl.glBindAttribLocation(programObject, sa.getIndex(), sa.getName());
            }
        }

        gl.glLinkProgram(programObject);
        gl.glValidateProgram(programObject);
        int[] error = new int[]{-1};
        gl.glGetProgramiv(programObject, GL2ES2.GL_LINK_STATUS, error, 0);
        if (error[0] == 0) {
            int[] len = new int[]{512};
            byte[] errormessage = new byte[512];
            gl.glGetProgramInfoLog(programObject, 512, len, 0, errormessage, 0);
            throw new BuildException(new String(errormessage, 0, len[0]), LinkTime);
        }

        return new ShaderProgramm(drawable, programObject);
    }
}
