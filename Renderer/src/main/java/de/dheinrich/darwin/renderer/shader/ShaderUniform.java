/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.renderer.shader;

import com.jogamp.opengl.util.GLBuffers;
import de.dheinrich.darwin.renderer.opengl.Element;
import de.dheinrich.darwin.renderer.opengl.ShaderProgramm;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.media.opengl.GLUniformData;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class ShaderUniform implements ShaderElement
{
    private final String name;
    private final Element element;
    private transient final GLUniformData data;
    private transient boolean changed = false;

    public ShaderUniform(String name, Element element) {
        this.name = name;
        this.element = element;

        if (element.gltype.ismatrix) {
            int s = (int) Math.sqrt(element.gltype.size);
            data = new GLUniformData(name, s, s,
                              GLBuffers.newDirectFloatBuffer(element.gltype.size));
        } else
            data = new GLUniformData(name, element.gltype.size,
                                     (FloatBuffer) null);
    }

    @Override
    public Element getElement() {
        return element;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getIndex() {
        return data.getLocation();
    }

    @Override
    public void ini(ShaderProgramm sp) {
        sp.use();
        data.setLocation(sp.getUniformLocation(name));
    }

    GLUniformData getData() {
        return data;
    }

    public void setData(int value){
        data.setData(value);
        changed = true;
    }

    public void setData(float value){
        data.setData(value);
        changed = true;
    }

    public void setData(float... array){
        data.setData(FloatBuffer.wrap(array));
        changed = true;
    }

    public void setData(FloatBuffer value){
        data.setData(value);
        changed = true;
    }

    public void setData(IntBuffer value){
        data.setData(value);
        changed = true;
    }

    public boolean wasChanged(){
        boolean res = changed;
        changed = false;
        return res;
    }

    @Override
    public String toString() {
        return name;
    }
}
