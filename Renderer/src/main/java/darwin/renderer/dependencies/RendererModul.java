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

import darwin.renderer.geometrie.attributs.StdAttributs.StdAttributsFactory;
import darwin.renderer.geometrie.attributs.VAOAttributs.VAOAttributsFactory;
import darwin.renderer.geometrie.attributs.VertexAttributs.VAttributsFactory;
import darwin.renderer.geometrie.factorys.Rahmen.RahmenFactory;
import darwin.renderer.geometrie.packed.RenderMesh.RenderMeshFactory;
import darwin.renderer.geometrie.packed.RenderModel.RenderModelFactory;
import darwin.renderer.opengl.FrameBuffer.*;
import darwin.renderer.opengl.FrameBuffer.RenderBuffer.RenderBufferFactory;
import darwin.renderer.opengl.VertexBO.VBOFactoy;
import darwin.renderer.opengl.buffer.BufferObject.BufferFactory;
import darwin.renderer.shader.Sampler.SamplerFactory;
import darwin.renderer.shader.Shader.ShaderFactory;
import darwin.resourcehandling.resmanagment.ROLoadJob.ROJobFactory;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import javax.media.opengl.GLProfile;

/**
 *
 * @author daniel
 */
public class RendererModul extends AbstractModule
{

    @Override
    protected void configure()
    {
        //TODO introduce annotation processor for automatic factory interface creation of @AssistedInject constructors
        Class[] factoryClasses = new Class[]
        {
            StdAttributsFactory.class,
            VAOAttributsFactory.class,
            VAttributsFactory.class,
            RenderMeshFactory.class,
            RenderModelFactory.class,
            BufferFactory.class,
            VBOFactoy.class,
            RahmenFactory.class,
            SamplerFactory.class,
            ROJobFactory.class,
            ShaderFactory.class,
            RenderBufferFactory.class,
        };

        for (Class factory : factoryClasses) {
            install(new FactoryModuleBuilder().build(factory));
        }

//        bind(ConfiguratorFactory.class).to(VAOAttributsFactory.class);

        bind(String.class).annotatedWith(Names.named("GL_Profile")).
                toInstance(GLProfile.GL2ES2);
//                toProvider(Providers.of((String)null));//null is highest available

        //TODO wieder verfuegbar machen, geht nicht da der Provider ausgefuehrt wird bevor der Graphic Context initialisiert wurde
//        bind(MemoryInfo.class).toProvider(MemoryInfoProvider.class).in(Singleton.class);

        bind(FrameBufferObject.class).annotatedWith(Default.class).to(DefaultFrameBuffer.class);
    }
}
