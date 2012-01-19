/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.renderer.shader;

import de.dheinrich.darwin.renderer.opengl.*;

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
        hash = 17 * hash + (element != null ? element.gltype.hashCode() : 0);
        hash = 17 * hash + index;
        return hash;
    }
}
