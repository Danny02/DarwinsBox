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
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.logging.*;
import java.util.regex.*;

import darwin.renderer.GraphicContext;
import darwin.renderer.shader.BuildException;
import darwin.util.misc.*;

import javax.inject.Inject;
import javax.media.opengl.*;

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

        int[] error = new int[]{-1};
        gl.glGetShaderiv(shader, GL2ES2.GL_COMPILE_STATUS, error, 0);

        if (error[0] != GL.GL_TRUE) {
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
            Pattern location = Pattern.compile("(\\d):(\\d+)");
            StringBuilder sb = new StringBuilder("<");
            try {
                String[][] texts = new String[sources.length][];
                for (int i = 0; i < sources.length; i++) {
                    texts[i] = sources[i].split("\n");
                }
                String line;
                while ((line = errors.readLine()) != null) {
                    sb.append("-\t").append(line).append('\n');
                    Matcher er = location.matcher(line);
                    if (er.find()) {
                        try {
                            int file = parseInt(er.group(1));//because we add the version tag infront of everything
                            int fLine = parseInt(er.group(2)) - 1;//don'T know why
                            String sline = texts[file][fLine];
                            sb.append("\t\t").append(sline).append('\n');
                        } catch (Throwable t) {
                        }
                    }
                }
            } catch (IOException ex) {
            }
            sb.append(">");
            String file = writeSourceFile(sources);
            if (file != null) {
                sb.append(" source: ").append(writeSourceFile(sources));
            }

            throw new BuildException(sb.toString(), BuildException.BuildError.CompileTime);
        }
    }

    private String writeSourceFile(String[] sources) {
        try {
            CompositIterator<String> c = new CompositIterator<>();
            for (String s : sources) {
                c.add(new ArrayIterator<>(s.split("\n")));
            }
            Iterable<String> lines = new IterableFacade(c);

            Path tmp = Files.createTempFile(null, null);
            Files.write(tmp, lines, Charset.defaultCharset());
            return tmp.toAbsolutePath().toString();
        } catch (IOException ex) {
        }
        return null;
    }
}
