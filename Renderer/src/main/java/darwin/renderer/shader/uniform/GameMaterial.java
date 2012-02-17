/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer.shader.uniform;

import java.io.Serializable;
import java.util.Arrays;

import darwin.renderer.geometrie.unpacked.ObjMaterial;
import darwin.resourcehandling.resmanagment.texture.ShaderDescription;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class GameMaterial implements Serializable
{
    private final float[] diffuse, ambient, specular;
    public final float specular_exponet;
    public final String diffuseTex;
    public final String specularTex;
    public final String normalTex;
    public final String alphaTex;
    public final ShaderDescription description;

    public GameMaterial(ObjMaterial mat, ShaderDescription description) {
        this.description = description;
        diffuse = trimToThree(mat.getDiffuse());
        ambient = trimToThree(mat.getAmbient());
        specular = trimToThree(mat.getSepcular());
        specular_exponet = mat.getSpecular_exponent();

        diffuseTex = mat.getDiffuseTex();
        specularTex = mat.getSpecularTex();
        alphaTex = mat.getAlphaTex();

        String nt = mat.getNormalTex();
        if(nt == null)
            normalTex = mat.getBumbTex();
        else
            normalTex = nt;
    }

    private float[] trimToThree(float[] arr){
        if(arr == null)
            return null;
        return new float[]{arr[0], arr[1], arr[2]};
    }

    public float[] getDiffuse() {
        return diffuse;
    }

    public float[] getAmbient() {
        return ambient;
    }

    public float[] getSpecular() {
        return specular;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final GameMaterial other = (GameMaterial) obj;
        if (!Arrays.equals(this.diffuse, other.diffuse))
            return false;
        if (!Arrays.equals(this.ambient, other.ambient))
            return false;
        if (!Arrays.equals(this.specular, other.specular))
            return false;
        if (this.specular_exponet != other.specular_exponet)
            return false;
        if ((this.diffuseTex == null) ? (other.diffuseTex != null) : !this.diffuseTex.equals(other.diffuseTex))
            return false;
        if ((this.specularTex == null) ? (other.specularTex != null) : !this.specularTex.equals(other.specularTex))
            return false;
        if ((this.normalTex == null) ? (other.normalTex != null) : !this.normalTex.equals(other.normalTex))
            return false;
        if ((this.alphaTex == null) ? (other.alphaTex != null) : !this.alphaTex.equals(other.alphaTex))
            return false;
        if (this.description != other.description && (this.description == null || !this.description.equals(other.description)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
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
        hash =
        97 * hash + (this.description != null ? this.description.hashCode() : 0);
        return hash;
    }

}
