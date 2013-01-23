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

import darwin.renderer.GraphicContext;
import darwin.util.logging.InjectLogger;

import com.google.common.base.Optional;
import javax.media.opengl.*;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

/**
 * CPU seitige Repr�sentation eines OpenGL Shader Programmes
 * <p/>
 * @author Daniel Heinrich
 */
public class ShaderProgramm {

    @InjectLogger
    private Logger logger = NOPLogger.NOP_LOGGER;

    static {
        assert GLProfile.isAvailable(GLProfile.GL2ES2) : "This device doesn't support Shaders";
    }
    private static ShaderProgramm shaderinuse = null;
    private final GraphicContext gc;
    private final int programObject;

    public ShaderProgramm(GraphicContext context, int programObject) {
        gc = context;
        this.programObject = programObject;
    }

    /**
     * @return Gibt den Index des Programm Objekt im Grafikspeicher zur�ck.
     */
    public int getPObject() {
        return programObject;
    }

    public int getUniformLocation(String s) {
        return gc.getGL().getGL2GL3().glGetUniformLocation(getPObject(), s);
    }

    /**
     * @param name Name der Attribut Variable(wie im Vertex ShaderProgramm
     * definiert).
     * <p/>
     * @return Gibt den Index der Attribut Varibale im Grafikspeicher zur�ck.
     */
    public int getAttrLocation(String name) {
        return gc.getGL().getGL2GL3().glGetAttribLocation(programObject, name);
    }

    /**
     * Aktiviert das ShaderProgramm Programm im GL Context.
     */
    public void use() {
        if (shaderinuse != this) {
            gc.getGL().getGL2GL3().glUseProgram(getPObject());
            shaderinuse = this;
        }
    }

    /**
     * Deaktiviert ShaderProgramm Programme im GL Context.
     */
    public void disable() {
        gc.getGL().getGL2GL3().glUseProgram(0);
        shaderinuse = null;
    }

    public void delete() {
        gc.getGL().getGL2GL3().glDeleteProgram(getPObject());
        if (shaderinuse == this) {
            disable();
        }
    }

    public Optional<String> verify() {
        GL2ES2 gl = gc.getGL().getGL2ES2();
        gl.glValidateProgram(programObject);
        int[] error = new int[]{-1};
        gl.glGetProgramiv(programObject, GL2GL3.GL_LINK_STATUS, error, 0);
        if (error[0] != GL.GL_TRUE) {
            int[] len = new int[1];
            gl.glGetProgramiv(programObject, GL2ES2.GL_INFO_LOG_LENGTH, len, 0);
            if (len[0] == 0) {
                return Optional.absent();
            }

            byte[] errormessage = new byte[len[0]];
            gl.glGetProgramInfoLog(programObject, len[0], len, 0, errormessage, 0);
            return Optional.of(new String(errormessage, 0, len[0]));
        }
        return Optional.absent();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
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
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + this.programObject;
        return hash;
    }
}
