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
package darwin.renderer.geometrie.packed;

import java.util.*;

import darwin.geometrie.unpacked.*;
import darwin.renderer.geometrie.packed.RenderMesh.RenderMeshFactory;
import darwin.renderer.opengl.VertexBO;
import darwin.renderer.opengl.VertexBO.VBOFactoy;
import darwin.renderer.opengl.buffer.BufferObject;
import darwin.renderer.opengl.buffer.BufferObject.BufferFactory;
import darwin.renderer.shader.*;
import darwin.renderer.shader.uniform.*;

import com.google.common.base.Optional;
import com.google.inject.assistedinject.*;
import com.jogamp.opengl.util.texture.Texture;

/**
 * Haelt alle Render relevanten Attribute eines 3D Modelles. Rendert ein Modell
 * nach diesen Attributen
 * <p/>
 * @author Daniel Heinrich
 */
public final class RenderModel implements Shaded, Cloneable {

    public interface RenderModelFactory {

        public RenderModel create(Model model, Shader shader);

        public RenderModel create(RenderMesh rbuffer, Shader shader, Material mat);
    }
    private final ShaderMaterialFactory factory;
     private Material material;
    private RenderMesh rbuffer;
     private Shader shader;
    private final Set<UniformSetter> uniforms = new HashSet<>();

    @AssistedInject
    public RenderModel(ShaderMaterialFactory factory,
                       @Assisted RenderMesh rbuffer, @Assisted Shader shader,
                       @Assisted  Material mat) {
        this.factory = factory;
        this.rbuffer = rbuffer;
        material = mat;
        setShader(shader);
    }

    @AssistedInject
    public RenderModel(RenderMeshFactory meshFactory, ShaderMaterialFactory factory,
                       VBOFactoy vboFactoy, BufferFactory bufferFactory,
                       @Assisted Model model, @Assisted Shader shader) {
        this.factory = factory;
        material = model.getMat();
        setShader(shader);

        Mesh m = model.getMesh();

        VertexBO vbo = vboFactoy.create(m.getVertices());
        int[] i = m.getIndicies();
        BufferObject indice = null;
        if (i != null) {
            indice = bufferFactory.buildIndiceBuffer(i);
        }

        rbuffer = meshFactory.create(shader, m.getPrimitiv_typ(), indice, vbo);
    }

    @Override
    public void render() {
        if (shader.isInitialized()) {
            for (UniformSetter us : uniforms) {
                us.set();
            }
            shader.updateUniformData();
            rbuffer.render();
        }
    }

    public void setShader(Shader shader) {
        this.shader = shader;
        if (material != null) {
            ShaderMaterial smaterial = factory.create(shader, material);
            uniforms.addAll(Arrays.asList(smaterial.getSetter()));
        }
    }

    public void addSamplerSetter(String s, Texture tc) {
        Optional<Sampler> sampler = shader.getSampler(s);
        if (sampler.isPresent()) {
            uniforms.add(new SamplerSetter(sampler.get(), tc));
        }
    }

    public void addUniformSetter(UniformSetter us) {
        uniforms.add(us);
    }

    @Override
    public Shader getShader() {
        return shader;
    }

    @Override
    public RenderModel clone() {
        return new RenderModel(factory, rbuffer.clone(), shader, material);
    }
}
