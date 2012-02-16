/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer.shader.uniform;

import com.jogamp.opengl.util.texture.*;
import darwin.renderer.shader.*;
import darwin.resourcehandling.resmanagment.texture.*;
import darwin.resourcehandling.wrapper.*;
import java.util.*;
import javax.media.opengl.*;

import static darwin.resourcehandling.resmanagment.RessourcesLoader.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
//TODO besseres Material System
public class ShaderMaterial {

    private final Shader shader;
    private final UniformSetter[] setter;

    public ShaderMaterial(Shader shader, List<UniformSetter> setter) {
        this.shader = shader;
        this.setter = new UniformSetter[setter.size()];
        setter.toArray(this.setter);
    }

    public ShaderMaterial(Shader shader, UniformSetter... setter) {
        this.shader = shader;
        this.setter = setter;
    }

    public ShaderMaterial(Shader shader, GameMaterial material) {
        this(shader, material, new LinkedList<UniformSetter>());
    }

    public ShaderMaterial(Shader shader, GameMaterial material,
            List<UniformSetter> setter) {
        this.shader = shader;
        add2List(setter, createSetter("mat_diffuse", material.getDiffuse()));
        add2List(setter, createSetter("mat_ambient", material.getAmbient()));
        add2List(setter, createSetter("mat_specular", material.getSpecular()));
        add2List(setter, createSetter("mat_spec_exp", material.specular_exponet));

        add2List(setter, createSetter("diffuse_sampler", material.diffuseTex));
        add2List(setter, createSetter("specular_sampler", material.specularTex));

        add2List(setter, createSetter("alpha_sampler", material.alphaTex));

        //TODO normalmapping
//        add2List(setter, createSetter("normal_sampler", material.normalTex));

        this.setter = new UniformSetter[setter.size()];
        setter.toArray(this.setter);
    }

    public ShaderMaterial(Shader s, Texture[] texs, String... unames) {
        shader = s;
        setter = new UniformSetter[texs.length];
        for (int i = 0; i < texs.length; i++) {
            setter[i] = createSet(unames[i], texs[i]);
        }
    }

    private SamplerSetter createSet(String n, Texture t) {
        return new SamplerSetter(shader.getSampler(n), t);
    }

    public void prepareShader() {
        for (UniformSetter us : setter) {
            us.set();
        }
    }

    public UniformSetter[] getSetter() {
        return setter;
    }

    private void add2List(List<UniformSetter> list, UniformSetter s) {
        if (s != null) {
            list.add(s);
        }
    }

    private UniformSetter createSetter(String name, float... value) {
        ShaderUniform u = shader.getUniform(name);
        if (u != null && value != null) {
            return new FloatSetter(u, value);
        }
        return null;
    }

    private UniformSetter createSetter(String name, String path) {
        if (path != null) {
            Sampler s = shader.getSampler(name);
            if (s != null) {
                TextureLoadJob tlj = new TextureLoadJob(path, GL.GL_LINEAR,
                        GL.GL_REPEAT);
                TextureContainer tc = RESOURCES.getTexture(tlj);
                return new SamplerSetter(s, tc);
            }
        }
        return null;
    }
}
