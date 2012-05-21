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

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLProfile;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import darwin.util.logging.InjectLogger;

/**
 * CPU seitige Repr�sentation eines OpenGL Shader Programmes
 * <p/>
 * @author Daniel Heinrich
 */
public class ShaderProgramm
{

    @InjectLogger
    private Logger logger = NOPLogger.NOP_LOGGER;

    static {
        assert GLProfile.isAvailable(GLProfile.GL2ES2) : "This device doesn't support Shaders";
    }
    private static ShaderProgramm shaderinuse = null;
    private final GLAutoDrawable drawable;
    private final int programObject;

    public ShaderProgramm(GLAutoDrawable drawable, int programObject)
    {
        this.drawable = drawable;
        this.programObject = programObject;
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
        return drawable.getGL().getGL2GL3().glGetUniformLocation(getPObject(), s);
    }

    /**
     * @param name Name der Attribut Variable(wie im Vertex ShaderProgramm
     *             definiert).
     * <p/>
     * @return Gibt den Index der Attribut Varibale im Grafikspeicher zur�ck.
     */
    public int getAttrLocation(String name)
    {
        return drawable.getGL().getGL2GL3().glGetAttribLocation(programObject, name);
    }

    /**
     * Aktiviert das ShaderProgramm Programm im GL Context.
     */
    public void use()
    {
        if (shaderinuse != this) {
            drawable.getGL().getGL2GL3().glUseProgram(getPObject());
            shaderinuse = this;
        }
    }

    /**
     * Deaktiviert ShaderProgramm Programme im GL Context.
     */
    public void disable()
    {
        drawable.getGL().getGL2GL3().glUseProgram(0);
        shaderinuse = null;
    }

    public void delete()
    {
        drawable.getGL().getGL2GL3().glDeleteProgram(getPObject());
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
        if(obj == this)
        	return true;
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
