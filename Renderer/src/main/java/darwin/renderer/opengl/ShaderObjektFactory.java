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
public class ShaderObjektFactory {

    private final GraphicContext gc;

    @Inject
    public ShaderObjektFactory(GraphicContext gcont) {
        gc = gcont;
    }

    public ShaderObjekt create(ShaderType type, String[] shadertext) throws BuildException {
        int glObjectID = compileShaderObject(type, shadertext);
        return new ShaderObjekt(type, glObjectID);
    }

    private int compileShaderObject(ShaderType type, String[] shaderText) throws BuildException {
        GL2GL3 gl = gc.getGL().getGL2GL3();

        int glObject = gl.glCreateShader(type.glConst);
        gl.glShaderSource(glObject, shaderText.length, shaderText, null);
        gl.glCompileShader(glObject);

        handleError(glObject, shaderText);

        return glObject;
    }

    private void handleError(int shader, String[] sources) throws BuildException {
        GL2GL3 gl = gc.getGL().getGL2GL3();

        int[] error = new int[1];
        gl.glGetShaderiv(shader, GL2ES2.GL_COMPILE_STATUS, error, 0);

        if (error[0] == GL.GL_FALSE) {
            int[] len = new int[1];
            gl.glGetShaderiv(shader, GL2ES2.GL_INFO_LOG_LENGTH, len, 0);
            if (len[0] == 0) {
                return;
            }

            byte[] errormessage = new byte[len[0]];
            gl.glGetShaderInfoLog(shader, len[0], len, 0, errormessage, 0);

            String tmp = new String(errormessage, 0, len[0] + 1);
            BufferedReader errors = new BufferedReader(new StringReader(tmp));

            //TODO build a Shader error parser for all graphic cards and so on
            StringBuilder sb = new StringBuilder("<");
            try {
                String[][] texts = new String[sources.length][];
                for (int i = 0; i < sources.length; i++) {
                    texts[i] = sources[i].split("\n");
                }
                String line;
                while ((line = errors.readLine()) != null) {
                    sb.append("-\t").append(line).append('\n');
                    String[] er = line.split(":");
                    if (er.length >= 4) {
                        try {
                            int file = parseInt(er[1].trim());//because we add the version tag infont of everything
                            int fLine = parseInt(er[2].split("\\(")[0]) - 1;//don'T know why
                            String sline = texts[file][fLine];
                            sb.append("\t\t").append(sline).append('\n');
                        } catch (Throwable t) {
                        }
                    }
                }
            } catch (IOException ex) {
            }
            throw new BuildException(sb.append(">").toString(), BuildException.BuildError.CompileTime);
        }
    }
}
