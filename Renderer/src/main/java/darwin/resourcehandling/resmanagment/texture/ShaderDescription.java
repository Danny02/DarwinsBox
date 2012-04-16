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
package darwin.resourcehandling.resmanagment.texture;

import java.io.Serializable;
import java.util.Arrays;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class ShaderDescription implements Serializable
{
    public final String v, f, g;
    public final String[] flags;

    public ShaderDescription(String s) {
        this(s, null);
    }

    public ShaderDescription(String s, String[] flags) {
        this(s + ".frag", s + ".vert", s + ".geom", flags);
    }

    public ShaderDescription(String f, String v, String g) {
        this(f, v, g, null);
    }

    public ShaderDescription(String f, String v, String g, String[] flags) {
        this.f = f;
        this.v = v;
        this.g = g;
        this.flags = flags != null ? flags : new String[0];
    }

    public ShaderDescription mergeFlags(String[] fl) {
        String[] newflags = new String[flags.length + fl.length];
        System.arraycopy(flags, 0, newflags, 0, flags.length);
        System.arraycopy(fl, 0, newflags, flags.length, fl.length);
        return new ShaderDescription(f, v, g, newflags);
    }

    @Override
    public String toString() {
        return f + ", " + v + ", " + g;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ShaderDescription other = (ShaderDescription) obj;
        if ((this.v == null) ? (other.v != null) : !this.v.equals(other.v))
            return false;
        if ((this.f == null) ? (other.f != null) : !this.f.equals(other.f))
            return false;
        if ((this.g == null) ? (other.g != null) : !this.g.equals(other.g))
            return false;
        if (!Arrays.deepEquals(this.flags, other.flags))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.v != null ? this.v.hashCode() : 0);
        hash = 79 * hash + (this.f != null ? this.f.hashCode() : 0);
        hash = 79 * hash + (this.g != null ? this.g.hashCode() : 0);
        hash = 79 * hash + Arrays.deepHashCode(this.flags);
        return hash;
    }
}
