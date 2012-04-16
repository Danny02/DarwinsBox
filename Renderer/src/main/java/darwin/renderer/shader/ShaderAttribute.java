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

import darwin.geometrie.data.Element;
import darwin.renderer.opengl.ShaderProgramm;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class ShaderAttribute implements ShaderElement {

    public final String name;
    public final Element element;
    transient private int index = -1;

    public ShaderAttribute(String name, Element element) {
        this.name = name;
        this.element = element;
    }

    public ShaderAttribute(String name, Element element, Integer index) {
        this(name, element);
        if (index != null) {
            this.index = index;
        }
    }

    @Override
    public void ini(ShaderProgramm sp) {
        sp.use();
        index = sp.getAttrLocation(name);
    }

    @Override
    public int getIndex() {
        return index;
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
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ShaderAttribute other = (ShaderAttribute) obj;
        if (this.element != other.element && (this.element == null || !this.element.equals(other.element))) {
            return false;
        }
        if (this.index != other.index) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (element != null ? element.getVectorType().hashCode() : 0);
        hash = 17 * hash + index;
        return hash;
    }
}
