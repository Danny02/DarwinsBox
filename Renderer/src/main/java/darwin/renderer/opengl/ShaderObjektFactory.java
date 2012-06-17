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

import darwin.renderer.GraphicContext;
import darwin.renderer.shader.BuildException;

import static java.lang.Integer.parseInt;

/**
 *
 * @author daniel
 */
public class ShaderObjektFactory
{
    private final static int ERROR_BUFFER_SIZE = 4096;
    private final GraphicContext gc;

    @Inject
    public ShaderObjektFactory(GraphicContext gcont)
    {
        gc = gcont;
    }

    public ShaderObjekt create(ShaderType type, String[] shadertext) throws BuildException
    {
        int glObjectID = compileShaderObject(gc.getGL().getGL2GL3(), type, shadertext);
        return new ShaderObjekt(gc.getGL().getGL2GL3(), type, glObjectID);
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
            int[] len = new int[]{ERROR_BUFFER_SIZE};
            byte[] errormessage = new byte[ERROR_BUFFER_SIZE];
            gl.glGetShaderInfoLog(globject, ERROR_BUFFER_SIZE, len, 0, errormessage, 0);
            String tmp = new String(errormessage, 0, len[0]);
            BufferedReader errors = new BufferedReader(new StringReader(tmp));

            //TODO build a Shader error parser for all graphic cards and so on
            StringBuilder sb = new StringBuilder();
            try {
                String[][] texts = new String[shadertext.length][];
                for (int i = 0; i < shadertext.length; i++) {
                    texts[i] = shadertext[i].split("\n");
                }
                String line;
                while ((line = errors.readLine()) != null) {
                    sb.append('\t').append(line).append('\n');
                    String[] er = line.split(":");
                    if (er.length >= 4) {
                        try {
                            int file = parseInt(er[0].trim())+1;//because we add the version tag infont of everything
                            int fLine = parseInt(er[1].split("\\(")[0]) - 2;//don'T know why
                            String sline = texts[file][fLine];
                            sb.append("\t\t").append(sline).append('\n');
                        } catch (Throwable t) {
                        }
                    }
                }
            } catch (IOException ex) {
            }
            throw new BuildException(sb.toString(), BuildException.BuildError.CompileTime);
        }
        return globject;
    }
}
