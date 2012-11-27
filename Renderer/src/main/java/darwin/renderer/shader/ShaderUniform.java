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
package darwin.renderer.shader;

import java.nio.*;

import darwin.renderer.opengl.*;

import com.jogamp.opengl.util.GLBuffers;
import javax.media.opengl.GLUniformData;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class ShaderUniform implements ShaderElement
{

    private final String name;
    private final GlElement element;
    private final GLUniformData data;
    private boolean changed = false;

    public ShaderUniform(String name, GlElement element)
    {
        this.name = name;
        this.element = element;

        int eleCount = element.getVectorType().getElementCount();
        if (element.isMatrix()) {
            int s = (int) Math.sqrt(eleCount);
            data = new GLUniformData(name, s, s,
                    GLBuffers.newDirectFloatBuffer(eleCount));
        } else {
            data = new GLUniformData(name, eleCount, (FloatBuffer) null);
        }
    }

    @Override
    public GlElement getElement()
    {
        return element;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public int getIndex()
    {
        return data.getLocation();
    }

    @Override
    public void ini(ShaderProgramm sp)
    {
        sp.use();
        data.setLocation(sp.getUniformLocation(name));
        changed = true;
    }

    GLUniformData getData()
    {
        return data;
    }

    public void setData(int value)
    {
        data.setData(value);
        changed = true;
    }

    public void setData(float value)
    {
        data.setData(value);
        changed = true;
    }

    public void setData(float... array)
    {
        data.setData(FloatBuffer.wrap(array));
        changed = true;
    }

    public void setData(FloatBuffer value)
    {
        data.setData(value);
        changed = true;
    }

    public void setData(IntBuffer value)
    {
        data.setData(value);
        changed = true;
    }

    public boolean wasChanged()
    {
        boolean res = changed;
        changed = false;
        return res;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
