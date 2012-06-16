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
package darwin.renderer.shader.uniform;

import com.jogamp.opengl.util.texture.Texture;
import java.util.*;
import javax.inject.*;
import javax.media.opengl.GL;

import darwin.geometrie.unpacked.Material;
import darwin.renderer.shader.*;
import darwin.resourcehandling.resmanagment.ResourcesLoader;
import darwin.resourcehandling.resmanagment.texture.TextureLoadJob;
import darwin.resourcehandling.resmanagment.texture.TextureLoadJob.TextureJobFactory;
import darwin.resourcehandling.wrapper.TextureContainer;

/**
 *
 * @author daniel
 */
@Singleton
public class ShaderMaterialFactory
{

    private final TextureJobFactory factory;
    private final ResourcesLoader loader;

    @Inject
    public ShaderMaterialFactory(TextureJobFactory factory, ResourcesLoader loader)
    {
        this.factory = factory;
        this.loader = loader;
    }

    public ShaderMaterial create(Shader shader, Collection<UniformSetter> setter)
    {
        UniformSetter[] array = new UniformSetter[setter.size()];
        setter.toArray(array);
        return new ShaderMaterial(shader, array);
    }

    public ShaderMaterial create(Shader shader, Material material)
    {
        return create(shader, material, new ArrayList<UniformSetter>());
    }

    public ShaderMaterial create(Shader shader, Material material,
            Collection<UniformSetter> setter)
    {
        ArrayList<UniformSetter> s = new ArrayList<>(setter);
        add2List(s, createSetter(shader, "mat_diffuse", material.getDiffuse()));
        add2List(s, createSetter(shader, "mat_ambient", material.getAmbient()));
        add2List(s, createSetter(shader, "mat_specular", material.getSpecular()));
        add2List(s, createSetter(shader, "mat_spec_exp", material.specular_exponet));

        add2List(s, createSetter(shader, "diffuse_sampler", material.diffuseTex));
        add2List(s, createSetter(shader, "specular_sampler", material.specularTex));

        add2List(s, createSetter(shader, "alpha_sampler", material.alphaTex));

        //TODO normalmapping
//        add2List(s, createSetter(shader, "normal_sampler", material.normalTex));

        return create(shader, s);
    }

    public ShaderMaterial create(Shader s, Texture[] texs, String... unames)
    {
        UniformSetter[] setter = new UniformSetter[texs.length];
        for (int i = 0; i < texs.length; i++) {
            setter[i] = new SamplerSetter(s.getSampler(unames[i]), texs[i]);
        }
        return new ShaderMaterial(s, setter);
    }

    private void add2List(Collection<UniformSetter> list, UniformSetter s)
    {
        if (s != null) {
            list.add(s);
        }
    }

    private UniformSetter createSetter(Shader shader, String name, float... value)
    {
        ShaderUniform u = shader.getUniform(name);
        if (u != null && value != null) {
            return new FloatSetter(u, value);
        }
        return null;
    }

    private UniformSetter createSetter(Shader shader, String name, String path)
    {
        if (path != null) {
            Sampler s = shader.getSampler(name);
            if (s != null) {
                TextureLoadJob tlj = factory.create(path, GL.GL_LINEAR, GL.GL_REPEAT);
                TextureContainer tc = loader.getTexture(tlj);
                return new SamplerSetter(s, tc);
            }
        }
        return null;
    }
}
