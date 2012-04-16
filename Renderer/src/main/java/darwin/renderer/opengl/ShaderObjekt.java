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
import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

import darwin.renderer.shader.BuildException;

import static darwin.renderer.GraphicContext.*;
import static darwin.renderer.shader.BuildException.BuildError.*;
import static java.lang.Integer.*;

/**
 * CPU seitige Repraesentation eines OpenGL Shader Objekts
 * <p/>
 * @author Daniel Heinrich
 */
public class ShaderObjekt
{

    private int type;
    private int globject;

    public ShaderObjekt(int type, String[] shadertext) throws BuildException
    {
        GL2ES2 gl = getGL().getGL2ES2();
        this.type = type;

        globject = gl.glCreateShader(type);
        gl.glShaderSource(globject, shadertext.length, shadertext, null);
        gl.glCompileShader(globject);
        int[] error = new int[1];
        gl.glGetShaderiv(globject, GL2ES2.GL_COMPILE_STATUS,
                error, 0);
        if (error[0] == GL.GL_FALSE) {
            int[] len = new int[]{512};
            byte[] errormessage = new byte[512];
            gl.glGetShaderInfoLog(globject, 512, len,
                    0, errormessage,
                    0);
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
            throw new BuildException(sb.toString(), CompileTime);
        }

    }

    public void delete()
    {
        getGL().getGL2GL3().glDeleteShader(getShaderobjekt());
    }

    public int getShaderobjekt()
    {
        return globject;
    }

    public int getType()
    {
        return type;
    }
}
