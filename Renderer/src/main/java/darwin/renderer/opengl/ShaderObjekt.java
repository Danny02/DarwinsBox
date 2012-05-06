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

import darwin.renderer.shader.BuildException;

/**
 * CPU seitige Repraesentation eines OpenGL Shader Objekts
 * <p/>
 * @author Daniel Heinrich
 */
public class ShaderObjekt
{
    private ShaderType type;
    private final int globject;
    private final GLAutoDrawable drawable;

    public ShaderObjekt(GLAutoDrawable drawable, ShaderType type, int glObjectID) throws BuildException
    {
        this.type = type;
        this.drawable = drawable;
        globject = glObjectID;
    }

    public void delete()
    {
        drawable.getGL().getGL2GL3().glDeleteShader(getShaderobjekt());
    }

    public int getShaderobjekt()
    {
        return globject;
    }

    public ShaderType getType()
    {
        return type;
    }
}
