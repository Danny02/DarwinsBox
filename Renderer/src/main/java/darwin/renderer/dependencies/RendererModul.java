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
package darwin.renderer.dependencies;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLDrawable;

import darwin.renderer.geometrie.attributs.*;
import darwin.renderer.geometrie.attributs.VertexAttributs.VAttributsFactory;
import darwin.renderer.geometrie.factorys.Rahmen.RahmenFactory;
import darwin.renderer.geometrie.packed.RenderMesh.RenderMeshFactory;
import darwin.renderer.geometrie.packed.RenderModel.RenderModelFactory;
import darwin.renderer.opengl.FrameBuffer.*;
import darwin.renderer.opengl.buffer.BufferObject.BufferFactory;
import darwin.renderer.shader.Sampler.SamplerFactory;
import darwin.renderer.shader.Shader.ShaderFactory;
import darwin.renderer.shader.uniform.ShaderMaterialFactory;
import darwin.renderer.util.memory.MemoryInfo;
import darwin.resourcehandling.resmanagment.ROLoadJob.ROJobFactory;
import darwin.resourcehandling.resmanagment.ShaderLoadJob.ShaderJobFactory;
import darwin.resourcehandling.resmanagment.texture.CubeMapJob.CubeMapFactory;
import darwin.resourcehandling.resmanagment.texture.HeightMapLoadJob.HeightMapFactory;
import darwin.resourcehandling.resmanagment.texture.TextureLoadJob.TextureJobFactory;
import darwin.resourcehandling.wrapper.TextureAtlas.AtlasFactory;
import darwin.util.logging.Slf4jTypeListener;

/**
 *
 * @author daniel
 */
public class RendererModul extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind(AttributsConfigurator.class).to(StdAttributs.class);
//        bind(AttributsConfigurator.class).to(VAOAttributs.class);
        install(new FactoryModuleBuilder().build(ConfiguratorFactory.class));
        install(new FactoryModuleBuilder().build(VAttributsFactory.class));

        install(new FactoryModuleBuilder().build(RenderMeshFactory.class));
        install(new FactoryModuleBuilder().build(RenderModelFactory.class));

        install(new FactoryModuleBuilder().build(BufferFactory.class));

        install(new FactoryModuleBuilder().build(RahmenFactory.class));
        install(new FactoryModuleBuilder().build(SamplerFactory.class));

        install(new FactoryModuleBuilder().build(ROJobFactory.class));
        install(new FactoryModuleBuilder().build(ShaderJobFactory.class));
        install(new FactoryModuleBuilder().build(ShaderFactory.class));
        install(new FactoryModuleBuilder().build(TextureJobFactory.class));
        install(new FactoryModuleBuilder().build(HeightMapFactory.class));
        install(new FactoryModuleBuilder().build(CubeMapFactory.class));
        install(new FactoryModuleBuilder().build(AtlasFactory.class));
        

        bindListener(Matchers.any(), new Slf4jTypeListener());

        bind(GLDrawable.class).to(GLAutoDrawable.class);
        bind(GLAutoDrawable.class).toProvider(DrawableProvider.class);
        bind(String.class).annotatedWith(Names.named("GL_Profile")).toInstance(null);//null is highest available

        bind(MemoryInfo.class).toProvider(MemoryInfoProvider.class);

        bind(FrameBufferObject.class).annotatedWith(Default.class).to(DefaultFrameBuffer.class);
    }
}
