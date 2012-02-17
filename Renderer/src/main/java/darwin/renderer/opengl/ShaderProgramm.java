/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer.opengl;

import java.util.List;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLProfile;
import org.apache.log4j.Logger;

import darwin.renderer.shader.BuildException;
import darwin.renderer.shader.ShaderAttribute;

import static darwin.renderer.GraphicContext.*;
import static darwin.renderer.shader.BuildException.BuildError.*;

/**
 * CPU seitige Repr�sentation eines OpenGL Shader Programmes
 * <p/>
 * @author Daniel Heinrich
 */
public class ShaderProgramm
{

    private static class Log
    {

        private static Logger ger = Logger.getLogger(ShaderProgramm.class);
    }

    static {
        assert GLProfile.isAvailable(GLProfile.GL2ES2) : "This device doesn't support Shaders";
    }
    static ShaderProgramm shaderinuse = null;
    private int programObject;

    /**
     * Erstellt eines ShaderProgramm Object. <br>
     * <p/>
     * @param gl  <br> Der GL Context in dem das Programm erstellt werden soll.
     *            <br>
     * @param vs  <br> Vertex Shader Objekt das gelinked werden soll. <br> Falls
     *            NULL wird kein Vertex Shader gelinked und die Fixed-Function
     *            Pipeline wird verwendet. <br> Vorsicht!! Die Fixed-Function
     *            Pipeline ist Depricated und ist ab OpenGL 3.2 nicht mehr
     *            vorhanden. <br>
     * @param fs  <br> Fragment Shader Objekt das gelinked werden soll. <br>
     *            Falls NULL wird kein Fragment Shader gelinked und die
     *            Fixed-Function Pipeline wird verwendet. <br> Vorsicht!! Die
     *            Fixed-Function Pipeline ist Depricated und ist ab OpenGL 3.2
     *            nicht mehr vorhanden. <br>
     * @param uni <br> Eine Liste von Uniform variable der ShaderProgramm deren
     *            positionen abgefragt werden sollen.
     */
    public ShaderProgramm(List<ShaderAttribute> attr, ShaderObjekt... sobject) throws BuildException
    {
        GL2ES2 gl = getGL().getGL2ES2();
        programObject = gl.glCreateProgram();

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
    }

    /**
     * @return Gibt den Index des Programm Objekt im Grafikspeicher zur�ck.
     */
    public int getPObject()
    {
        return programObject;
    }

    public int getUniformLocation(String s)
    {
        return getGL().getGL2GL3().glGetUniformLocation(getPObject(), s);
    }

    /**
     * @param name Name der Attribut Variable(wie im Vertex ShaderProgramm
     *             definiert).
     * <p/>
     * @return Gibt den Index der Attribut Varibale im Grafikspeicher zur�ck.
     */
    public int getAttrLocation(String name)
    {
        return getGL().getGL2GL3().glGetAttribLocation(programObject, name);
    }

    /**
     * Aktiviert das ShaderProgramm Programm im GL Context.
     */
    public void use()
    {
        if (shaderinuse != this) {
            getGL().getGL2GL3().glUseProgram(getPObject());
            shaderinuse = this;
        }
    }

    /**
     * Deaktiviert ShaderProgramm Programme im GL Context.
     */
    public void disable()
    {
        getGL().getGL2GL3().glUseProgram(0);
        shaderinuse = null;
    }

    public void delete()
    {
        getGL().getGL2GL3().glDeleteProgram(getPObject());
        if (shaderinuse == this) {
            disable();
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ShaderProgramm other = (ShaderProgramm) obj;
        if (this.programObject != other.programObject) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 41 * hash + this.programObject;
        return hash;
    }
}
