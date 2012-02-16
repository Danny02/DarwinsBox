/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer.opengl;

import darwin.renderer.shader.*;
import java.io.*;
import javax.media.opengl.*;

import static darwin.renderer.GraphicContext.*;
import static darwin.renderer.shader.BuildException.BuildError.*;
import static java.lang.Integer.*;

/**
 * CPU seitige Repraesentation eines OpenGL Shader Objekts
 * @author Daniel Heinrich
 */
public class ShaderObjekt
{
    private int type;
    private int globject;

    public ShaderObjekt(int type, String[] shadertext) throws BuildException {
        GL2GL3 gl = getGL().getGL2GL3();
        this.type = type;

        globject = gl.glCreateShader(type);
        gl.glShaderSource(globject, shadertext.length, shadertext, null);
        gl.glCompileShader(globject);
        int[] error = new int[1];
        gl.glGetShaderiv(globject, GL2GL3.GL_COMPILE_STATUS,
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
                for (int i = 0; i < shadertext.length; i++)
                    texts[i] = shadertext[i].split("\n");
                String line;
                while ((line = errors.readLine()) != null) {
                    String[] er = line.split(":");
                    if (er.length < 4)
                        break;
                    String sline =
                           texts[parseInt(er[1].trim())][parseInt(er[2]) - 1];
                    sb.append('\t').append(line).append('\n');
                    sb.append("\t\t").append(sline).append('\n');
                }
            } catch (IOException ex) {
            }
            throw new BuildException(sb.toString(), CompileTime);
        }

    }

    public void delete() {
        getGL().getGL2GL3().glDeleteShader(getShaderobjekt());
    }

    public int getShaderobjekt() {
        return globject;
    }

    public int getType() {
        return type;
    }
}
