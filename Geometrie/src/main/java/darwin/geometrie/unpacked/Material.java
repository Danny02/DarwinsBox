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
package darwin.geometrie.unpacked;

import java.io.Serializable;
import java.util.Arrays;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class Material implements Serializable
{

    public final String name;
    private final float[] diffuse, ambient, specular;
    public final float specular_exponet;
    public final String diffuseTex;
    public final String specularTex;
    public final String normalTex;
    public final String alphaTex;

    public Material(String name, float[] diffuse, float[] ambient, float[] specular, float specular_exponet, String diffuseTex, String specularTex, String normalTex, String alphaTex)
    {
        this.name = name;
        this.diffuse = diffuse;
        this.ambient = ambient;
        this.specular = specular;
        this.specular_exponet = specular_exponet;
        this.diffuseTex = diffuseTex;
        this.specularTex = specularTex;
        this.normalTex = normalTex;
        this.alphaTex = alphaTex;
    }

    public float[] getDiffuse()
    {
        return diffuse;
    }

    public float[] getAmbient()
    {
        return ambient;
    }

    public float[] getSpecular()
    {
        return specular;
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
        final Material other = (Material) obj;
        if (!Arrays.equals(this.diffuse, other.diffuse)) {
            return false;
        }
        if (!Arrays.equals(this.ambient, other.ambient)) {
            return false;
        }
        if (!Arrays.equals(this.specular, other.specular)) {
            return false;
        }
        if (this.specular_exponet != other.specular_exponet) {
            return false;
        }
        if ((this.diffuseTex == null) ? (other.diffuseTex != null) : !this.diffuseTex.equals(other.diffuseTex)) {
            return false;
        }
        if ((this.specularTex == null) ? (other.specularTex != null) : !this.specularTex.equals(other.specularTex)) {
            return false;
        }
        if ((this.normalTex == null) ? (other.normalTex != null) : !this.normalTex.equals(other.normalTex)) {
            return false;
        }
        if ((this.alphaTex == null) ? (other.alphaTex != null) : !this.alphaTex.equals(other.alphaTex)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 97 * hash + Arrays.hashCode(this.diffuse);
        hash = 97 * hash + Arrays.hashCode(this.ambient);
        hash = 97 * hash + Arrays.hashCode(this.specular);
        hash = 97 * hash + Float.floatToIntBits(this.specular_exponet);
        hash =
                97 * hash + (this.diffuseTex != null ? this.diffuseTex.hashCode() : 0);
        hash =
                97 * hash + (this.specularTex != null ? this.specularTex.hashCode() : 0);
        hash =
                97 * hash + (this.normalTex != null ? this.normalTex.hashCode() : 0);
        hash =
                97 * hash + (this.alphaTex != null ? this.alphaTex.hashCode() : 0);
        return hash;
    }
}
