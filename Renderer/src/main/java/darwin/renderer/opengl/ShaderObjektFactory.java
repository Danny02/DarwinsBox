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

import java.io.*;
import javax.inject.Inject;
import javax.media.opengl.*;

import darwin.renderer.shader.BuildException;

import static java.lang.Integer.*;

/**
 *
 * @author daniel
 */
public class ShaderObjektFactory
{

    private final GLAutoDrawable drawable;

    @Inject
    public ShaderObjektFactory(GLAutoDrawable drawable)
    {
        this.drawable = drawable;
    }

    public ShaderObjekt create(ShaderType type, String[] shadertext) throws BuildException
    {
        int glObjectID = compileShaderObject(drawable.getGL().getGL2GL3(), type, shadertext);
        return new ShaderObjekt(drawable, type, glObjectID);
    }

    public static int compileShaderObject(GL2GL3 gl, ShaderType type,
            String[] shadertext) throws BuildException
    {
        int globject = gl.glCreateShader(type.glConst);
        gl.glShaderSource(globject, shadertext.length, shadertext, null);
        gl.glCompileShader(globject);
        int[] error = new int[1];
        gl.glGetShaderiv(globject, GL2ES2.GL_COMPILE_STATUS, error, 0);
        if (error[0] == GL.GL_FALSE) {
            int[] len = new int[]{512};
            byte[] errormessage = new byte[512];
            gl.glGetShaderInfoLog(globject, 512, len, 0, errormessage, 0);
            String tmp = new String(errormessage, 0, len[0]);
            BufferedReader errors = new BufferedReader(new StringReader(tmp));

            StringBuilder sb = new StringBuilder();
            try {
                sb.append('\t').append(errors.readLine()).append('\n');
                String[][] texts = new String[shadertext.length][];
                for (int i = 0; i < shadertext.length; i++) {
                    texts[i] = shadertext[i].split("\n");
                }
                String line;
                while ((line = errors.readLine()) != null) {
                    String[] er = line.split(":");
                    if (er.length < 4) {
                        break;
                    }
                    String sline = texts[parseInt(er[1].trim())][parseInt(er[2]) - 1];
                    sb.append('\t').append(line).append('\n');
                    sb.append("\t\t").append(sline).append('\n');
                }
            } catch (IOException ex) {
            }
            throw new BuildException(sb.toString(), BuildException.BuildError.CompileTime);
        }
        return globject;
    }
}
