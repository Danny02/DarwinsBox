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
package darwin.resourcehandling.wrapper;

/**
 *
 * @author dheinrich
 */
public class TextureAtlasElement
{
    transient public final float woffset, hoffset, width, heigth;
    private String name;

    public TextureAtlasElement(float woffset, float hoffset, float width,
                               float heigth, String name) {
        this.woffset = woffset;
        this.hoffset = hoffset;
        this.width = width;
        this.heigth = heigth;
        this.name = name;
    }

    public float[] getTexCoord(float s, float t) {
        return new float[]{woffset + s * width, hoffset + t * heigth};
    }

    public float[] getRelativeTexCoord(float s, float t) {
        return new float[]{s * width, t * heigth};
    }

    public String getName() {
        return name;
    }

    public float getHeigth() {
        return heigth;
    }

    public float getHoffset() {
        return hoffset;
    }

    public float getWidth() {
        return width;
    }

    public float getWoffset() {
        return woffset;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TextureAtlasElement other = (TextureAtlasElement) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}