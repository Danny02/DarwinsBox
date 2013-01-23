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
package darwin.geometrie.io.obj;

import java.io.*;
import java.util.Arrays;

import darwin.geometrie.unpacked.Material;

import static java.lang.Math.min;

/**
 * H�lt alle Werte eines in einem MTL File definierten Materials
 * <p/>
 * @author Daniel Heinrich
 */
public final class ObjMaterial implements Externalizable, Cloneable {

    /**
     * Ns exponent
     * <p/>
     * Specifies the specular exponent for the current material. This defines
     * the focus of the specular highlight.
     * <p/>
     * "exponent" is the value for the specular exponent. A high exponent
     * results in a tight, concentrated highlight. Ns values normally range from
     * 0 to 1000.
     */
    private float specular_exponent = 20; //Ns
    /**
     * Ni optical_density
     * <p/>
     * Specifies the optical density for the surface. This is also known as
     * index of refraction.
     * <p/>
     * "optical_density" is the value for the optical density. The values can
     * range from 0.001 to 10. A value of 1.0 means that light does not bend as
     * it passes through an object. Increasing the optical_density increases the
     * amount of bending. Glass has an index of refraction of about 1.5. Values
     * of less than 1.0 produce bizarre results and are not recommended.
     */
    private float optical_density; //Ni
    /**
     * sharpness value
     * <p/>
     * Specifies the sharpness of the reflections from the local reflection map.
     * If a material does not have a local reflection map defined in its
     * material definition, sharpness will apply to the global reflection map
     * defined in PreView.
     * <p/>
     * "value" can be a number from 0 to 1000. The default is 60. A high value
     * results in a clear reflection of objects in the reflection map.
     * <p/>
     * Tip	Sharpness values greater than 100 map introduce aliasing effects in
     * flat surfaces that are viewed at a sharp angle
     */
    private float sharpness; //sharpness
    /**
     * d -halo factor
     * <p/>
     * Specifies that a dissolve is dependent on the surface orientation
     * relative to the viewer. For example, a sphere with the following
     * dissolve, d -halo 0.0, will be fully dissolved at its center and will
     * appear gradually more opaque toward its edge.
     * <p/>
     * "factor" is the minimum amount of dissolve applied to the material.
     * dheinrich.own.engineween 1.0 (fully opaque) and the specified "factor".
     * The formula is:
     * <p/>
     * dissolve = 1.0 - (N*v)(1.0-factor)
     * <p/>
     * For a definition of terms, see "Illumination models" on page 5-30.
     */
    private float alpha; //d
    /**
     * illum: 0	Color on and Ambient off 1	Color on and Ambient on 2	Highlight
     * on 3	Reflection on and Ray trace on 4	Transparency: Glass on Reflection:
     * Ray trace on 5	Reflection: Fresnel on and Ray trace on 6	Transparency:
     * Refraction on Reflection: Fresnel off and Ray trace on 7	Transparency:
     * Refraction on Reflection: Fresnel on and Ray trace on 8	Reflection on and
     * Ray trace off 9	Transparency: Glass on Reflection: Ray trace off 10	Casts
     * shadows onto invisible surfaces
     */
    private byte illum;
    private float[] ambient;//ka
    private float[] diffuse;//kd
    private float[] sepcular = new float[]{0.2f, 0.2f, 0.2f};//ks
    private float[] emission;//ke
    transient private String ambientTex;  // map_Ka
    transient private String diffuseTex;  //map_Kd
    transient private String specularTex; //map_Ks
    transient private String normalTex;   //map_bump
    transient private String alphaTex;   //map_d
    transient private String bumpTex;     //bump
    transient private String name;

    public ObjMaterial() {
    }

    public ObjMaterial(String name) {
        this.name = name;
    }

    public ObjMaterial(float[] diffuse, float[] ambient, float[] emission,
                       float[] specular,
                       float optical_density, float sharpness, float alpha,
                       byte illum, String ambientTex, String diffuseTex,
                       String specularTex, String normalTex, String alphaTex,
                       String bumpTex, String name) {
        setDiffuse(diffuse);
        setAmbient(ambient);
        setEmission(emission);
        setSepcular(sepcular);
        this.optical_density = optical_density;
        this.sharpness = sharpness;
        this.alpha = alpha;
        this.illum = illum;
        this.ambientTex = ambientTex;
        this.diffuseTex = diffuseTex;
        this.specularTex = specularTex;
        this.normalTex = normalTex;
        this.alphaTex = alphaTex;
        this.bumpTex = bumpTex;
        this.name = name;
    }

    public Material creatGameMaterial() {
        String nt = getNormalTex();
        if (nt == null) {
            nt = getBumbTex();
        }
        return new Material(name, getSpecularExponent(), getDiffuse(), getAmbient(),
                            getSepcular(), getDiffuseTex(), getSpecularTex(),
                            nt, getAlphaTex());

    }

    private float[] forceThreeElements(float[] arr) {
        float[] res = new float[3];
        int min = min(3, arr.length);
        int i = 0;
        for (; i < min; ++i) {
            res[i] = arr[i];
        }
        for (; i < 3; ++i) {
            res[i] = 0;
        }
        return res;
    }

    public String getName() {
        return name;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public String getAlphaTex() {
        return alphaTex;
    }

    public void setAlphaTex(String alphaTex) {
        this.alphaTex = alphaTex;
    }

    public float[] getAmbient() {
        return ambient;
    }

    public void setAmbient(float[] ambient) {
        this.ambient = forceThreeElements(ambient);
    }

    public String getAmbientTex() {
        return ambientTex;
    }

    public void setAmbientTex(String ambientTex) {
        this.ambientTex = ambientTex;
    }

    public String getBumbTex() {
        return bumpTex;
    }

    public void setBumbTex(String bumbTex) {
        this.bumpTex = bumbTex;
    }

    public float[] getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(float[] diffuse) {
        this.diffuse = forceThreeElements(diffuse);
    }

    public String getDiffuseTex() {
        return diffuseTex;
    }

    public void setDiffuseTex(String diffuseTex) {
        this.diffuseTex = diffuseTex;
    }

    public float[] getEmission() {
        return emission;
    }

    public void setEmission(float[] emission) {
        this.emission = forceThreeElements(emission);
    }

    public byte getIllum() {
        return illum;
    }

    public void setIllum(byte illum) {
        this.illum = illum;
    }

    public String getNormalTex() {
        return normalTex;
    }

    public void setNormalTex(String normalTex) {
        this.normalTex = normalTex;
    }

    public float getOptical_density() {
        return optical_density;
    }

    public void setOptical_density(float optical_density) {
        this.optical_density = optical_density;
    }

    public float[] getSepcular() {
        return sepcular;
    }

    public void setSepcular(float[] sepcular) {
        this.sepcular = forceThreeElements(sepcular);
    }

    public float getSharpness() {
        return sharpness;
    }

    public void setSharpness(float sharpness) {
        this.sharpness = sharpness;
    }

    public String getSpecularTex() {
        return specularTex;
    }

    public void setSpecularTex(String specularTex) {
        this.specularTex = specularTex;
    }

    public float getSpecularExponent() {
        return specular_exponent;
    }

    public void setSpecular_exponent(float specular_exponent) {
        this.specular_exponent = specular_exponent;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public ObjMaterial clone() {
        return new ObjMaterial(diffuse.clone(), ambient.clone(),
                               emission.clone(), sepcular.clone(),
                               optical_density, sharpness, alpha, illum,
                               ambientTex,
                               diffuseTex, specularTex, normalTex, alphaTex,
                               bumpTex, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ObjMaterial other = (ObjMaterial) obj;
        if (this.specular_exponent != other.specular_exponent) {
            return false;
        }
        if (this.optical_density != other.optical_density) {
            return false;
        }
        if (this.sharpness != other.sharpness) {
            return false;
        }
        if (this.alpha != other.alpha) {
            return false;
        }
        if (this.illum != other.illum) {
            return false;
        }
        if (!Arrays.equals(this.ambient, other.ambient)) {
            return false;
        }
        if (!Arrays.equals(this.diffuse, other.diffuse)) {
            return false;
        }
        if (!Arrays.equals(this.sepcular, other.sepcular)) {
            return false;
        }
        if (!Arrays.equals(this.emission, other.emission)) {
            return false;
        }
        if ((this.ambientTex == null) ? (other.ambientTex != null) : !this.ambientTex.equals(other.ambientTex)) {
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
        if ((this.name == null) ? (other.name != null) : !this.name.equals(
                other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Float.floatToIntBits(this.specular_exponent);
        hash = 29 * hash + Float.floatToIntBits(this.optical_density);
        hash = 29 * hash + Float.floatToIntBits(this.sharpness);
        hash = 29 * hash + Float.floatToIntBits(this.alpha);
        hash = 29 * hash + this.illum;
        hash = 29 * hash + Arrays.hashCode(this.ambient);
        hash = 29 * hash + Arrays.hashCode(this.diffuse);
        hash = 29 * hash + Arrays.hashCode(this.sepcular);
        hash = 29 * hash + Arrays.hashCode(this.emission);
        hash =
        29 * hash + (this.ambientTex != null ? this.ambientTex.hashCode() : 0);
        hash =
        29 * hash + (this.diffuseTex != null ? this.diffuseTex.hashCode() : 0);
        hash =
        29 * hash + (this.specularTex != null ? this.specularTex.hashCode() : 0);
        hash =
        29 * hash + (this.normalTex != null ? this.normalTex.hashCode() : 0);
        hash =
        29 * hash + (this.alphaTex != null ? this.alphaTex.hashCode() : 0);
        hash = 29 * hash + (this.bumpTex != null ? this.bumpTex.hashCode() : 0);
        hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeFloat(specular_exponent);
        out.writeFloat(optical_density);
        out.writeFloat(sharpness);
        out.writeFloat(alpha);
        out.writeByte(illum);
        out.writeObject(ambient);
        out.writeObject(diffuse);
        out.writeObject(sepcular);
        out.writeObject(emission);

        int b = 0;
        b = b | ((name != null) ? 1 : 0);
        b = b | (((bumpTex != null) ? 1 : 0) << 1);
        b = b | (((alphaTex != null) ? 1 : 0) << 2);
        b = b | (((normalTex != null) ? 1 : 0) << 3);
        b = b | (((specularTex != null) ? 1 : 0) << 4);
        b = b | (((diffuseTex != null) ? 1 : 0) << 5);
        b = b | (((ambientTex != null) ? 1 : 0) << 6);
        out.writeByte(b);

        if ((name != null)) {
            out.writeUTF(name);
        }
        if ((bumpTex != null)) {
            out.writeUTF(bumpTex);
        }
        if ((alphaTex != null)) {
            out.writeUTF(alphaTex);
        }
        if ((normalTex != null)) {
            out.writeUTF(normalTex);
        }
        if ((specularTex != null)) {
            out.writeUTF(specularTex);
        }
        if ((diffuseTex != null)) {
            out.writeUTF(diffuseTex);
        }
        if ((ambientTex != null)) {
            out.writeUTF(ambientTex);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        specular_exponent = in.readFloat();
        optical_density = in.readFloat();
        sharpness = in.readFloat();
        alpha = in.readFloat();
        illum = in.readByte();
        ambient = (float[]) in.readObject();
        diffuse = (float[]) in.readObject();
        sepcular = (float[]) in.readObject();
        emission = (float[]) in.readObject();

        int b = in.readByte();
        if (0 != (b & 1)) {
            name = in.readUTF();
        }
        if (0 != (b & 2)) {
            bumpTex = in.readUTF();
        }
        if (0 != (b & 4)) {
            alphaTex = in.readUTF();
        }
        if (0 != (b & 8)) {
            normalTex = in.readUTF();
        }
        if (0 != (b & 16)) {
            specularTex = in.readUTF();
        }
        if (0 != (b & 32)) {
            diffuseTex = in.readUTF();
        }
        if (0 != (b & 64)) {
            ambientTex = in.readUTF();
        }
    }
}
