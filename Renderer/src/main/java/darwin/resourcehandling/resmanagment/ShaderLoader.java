/*
 * Copyright (C) 2012 some
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
package darwin.resourcehandling.resmanagment;

import java.io.IOException;
import java.nio.file.*;

import darwin.renderer.GraphicContext;
import darwin.renderer.opengl.ShaderProgramm;
import darwin.renderer.shader.Shader;
import darwin.renderer.shader.Shader.ShaderFactory;
import darwin.resourcehandling.ResourceChangeListener;
import darwin.resourcehandling.core.ResourceHandle;
import darwin.resourcehandling.handle.FileHandlerFactory;
import darwin.resourcehandling.io.ShaderFile.Builder;
import darwin.resourcehandling.io.*;
import darwin.resourcehandling.resmanagment.texture.ShaderDescription;
import darwin.util.logging.InjectLogger;

import javax.inject.Inject;
import javax.media.opengl.*;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

/**
 *
 * @author some
 */
public class ShaderLoader {

    private static final Path SHADER_PATH = Paths.get("resources/shaders");
    @InjectLogger
    private Logger logger = NOPLogger.NOP_LOGGER;
    private final FileHandlerFactory fileFactory;
    private final ShaderUtil util;
    private final ShaderFactory shaderFactory;
    private final GraphicContext gcontext;

    @Inject
    public ShaderLoader(FileHandlerFactory fileFactory, ShaderUtil util, ShaderFactory shaderFactory, GraphicContext gcontext) {
        this.fileFactory = fileFactory;
        this.util = util;
        this.shaderFactory = shaderFactory;
        this.gcontext = gcontext;
    }

    public Shader loadShader(ShaderDescription descr) throws IOException {
        ResourceHandle fragment = null, vertex = null, geometrie = null;
        if (descr.f != null) {
            fragment = fileFactory.create(SHADER_PATH.resolve(descr.f));
        }
        if (descr.v != null) {
            vertex = fileFactory.create(SHADER_PATH.resolve(descr.v));
        }
        if (descr.g != null) {
            geometrie = fileFactory.create(SHADER_PATH.resolve(descr.g));
        }

        ShaderReLoader reloader = new ShaderReLoader(fragment, vertex, geometrie);
        return reloader.createShader(descr.toString());
    }

    private class ShaderReLoader implements ResourceChangeListener {

        private Shader shader;
        private String name;
        private final ResourceHandle fragment, vertex, geometrie;

        public ShaderReLoader(ResourceHandle fragment, ResourceHandle vertex, ResourceHandle geometrie) {
            this.fragment = fragment;
            this.vertex = vertex;
            this.geometrie = geometrie;

            if (fragment != null) {
                fragment.registerChangeListener(this);
            }
            if (vertex != null) {
                vertex.registerChangeListener(this);
            }
            if (geometrie != null) {
                geometrie.registerChangeListener(this);
            }
        }

        public ShaderFile buildFile(String name) throws IOException {
            Builder builder = Builder.create(name);

            if (fragment != null) {
                builder.withFragment(util.getData(fragment.getStream()));
            }
            if (vertex != null) {
                builder.withVertex(util.getData(vertex.getStream()));
            }
            if (geometrie != null) {
                builder.withGeometrie(util.getData(geometrie.getStream()));
            }

            return builder.create();
        }

        public Shader createShader(String name) throws IOException {
            if (shader == null) {
                this.name = null;
                ShaderFile file = buildFile(name);
                shader = shaderFactory.create(file);
                compile(file);
            }
            return shader;
        }

        @Override
        public void resourceChanged(ResourceHandle handle) {
            try {
                compile(buildFile(name));
            } catch (IOException ex) {
                logger.warn(ex.getLocalizedMessage(), ex);
            }
        }

        private void compile(final ShaderFile file) {
            gcontext.getGLWindow().invoke(false, new GLRunnable() {
                @Override
                public boolean run(GLAutoDrawable glad) {
                    try {
                        ShaderProgramm compiledShader = util.compileShader(file);
                        ShaderProgramm old = shader.getProgramm();
                        shader.ini(compiledShader);
                        logger.info("Shader " + name + " was succesfully compiled!");
                        if (old != null) {
                            old.delete();
                        }
                    } catch (Throwable ex) {
                        logger.warn(ex.getLocalizedMessage());
                    }
                    return true;
                }
            });
        }
    }
}
