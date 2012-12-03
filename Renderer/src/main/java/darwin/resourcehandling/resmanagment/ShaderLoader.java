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
import java.util.logging.Level;

import darwin.renderer.GraphicContext;
import darwin.renderer.opengl.*;
import darwin.renderer.shader.Shader;
import darwin.renderer.shader.Shader.ShaderFactory;
import darwin.renderer.shader.ShaderProgramBuilder;
import darwin.resourcehandling.ResourceHandle;
import darwin.resourcehandling.dependencies.annotation.InjectBundle;
import darwin.resourcehandling.factory.ResourceFromBundle;
import darwin.resourcehandling.factory.ResourceWrapper;
import darwin.resourcehandling.handle.ResourceBundle;
import darwin.resourcehandling.io.ShaderFile;
import darwin.resourcehandling.io.ShaderFile.Builder;
import darwin.resourcehandling.io.ShaderUtil;
import darwin.util.logging.InjectLogger;

import com.google.common.base.Equivalence.Wrapper;
import com.google.common.base.Optional;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import javax.media.opengl.*;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

/**
 *
 * @author some
 */
public class ShaderLoader implements ResourceFromBundle<Shader> {

    @InjectBundle(value = "Empty.frag,Empty.vert", prefix = ShaderUtil.SHADER_PATH_PREFIX)
    private ResourceBundle empty;
    @InjectLogger
    private Logger logger = NOPLogger.NOP_LOGGER;
    private final ShaderUtil util;
    private final ShaderFactory shaderFactory;
    private final GraphicContext gcontext;
    private final String[] mutations;

    public interface ShaderLoaderFactory {

        public ShaderLoader create(String... mutations);
    }

    @AssistedInject
    public ShaderLoader(ShaderUtil util, ShaderFactory shaderFactory, GraphicContext gcontext, @Assisted String[] mutations) {
        this.util = util;
        this.shaderFactory = shaderFactory;
        this.gcontext = gcontext;
        this.mutations = mutations;
    }

    @Override
    public Shader create(ResourceBundle bundle) throws IOException {
        Builder builder = new Builder();
        switch (bundle.getCount()) {
            default:
            case 3:
                builder.withGeometrie(util.getData(bundle.get(2).getStream()));
            case 2:
                builder.withVertex(util.getData(bundle.get(1).getStream()));
            case 1:
                builder.withFragment(util.getData(bundle.get(0).getStream()));
        }
        builder.withName(getName(bundle)).withMutations(mutations);

        final ShaderFile file = builder.create();
        final Shader shader = shaderFactory.create(file);

        gcontext.getGLWindow().invoke(false, new GLRunnable() {
            @Override
            public boolean run(GLAutoDrawable glad) {
                try {
                    ShaderProgramm compiledShader = util.compileShader(file);
                    ShaderProgramm old = shader.getProgramm();
                    shader.ini(compiledShader);
                    logger.info("Shader " + file.name + " was succesfully compiled!");
                    if (old != null) {
                        old.delete();
                    }
                } catch (Throwable ex) {
                    logger.warn(ex.getLocalizedMessage());
                }
                return true;
            }
        });

        return shader;
    }

    @Override
    public void update(final ResourceBundle bundle, ResourceHandle changed, final ResourceWrapper<Shader> wrapper) {
        final Shader shader = wrapper.get();
        try {
            String d = null;
            ShaderType t = null;
            switch (bundle.getCount()) {
                default:
                case 3:
                    if (bundle.get(2) == changed) {
                        d = util.getData(bundle.get(2).getStream());
                        t = ShaderType.Geometrie;
                        break;
                    }
                case 2:
                    if (bundle.get(1) == changed) {
                        d = util.getData(bundle.get(1).getStream());
                        t = ShaderType.Vertex;
                        break;
                    }
                case 1:
                    if (bundle.get(0) == changed) {
                        d = util.getData(bundle.get(0).getStream());
                        t = ShaderType.Fragment;
                        break;
                    }
            }

            final ShaderType type = t;
            final String data = d;
            gcontext.getGLWindow().invoke(false, new GLRunnable() {
                @Override
                public boolean run(GLAutoDrawable glad) {
                    try {
                        GL2GL3 gl = glad.getGL().getGL2GL3();
                        ShaderObjekt so = util.createSObject(type, data, mutations);
                        int po = shader.getProgramm().getPObject();

                        gl.glAttachShader(po, so.getShaderobjekt());
                        gl.glLinkProgram(po);
                        shader.ini(shader.getProgramm());
                        
                        Optional<String> error = shader.getProgramm().verify();
                        if (error.isPresent()) {
                            logger.warn(error.get());
                            wrapper.set(getFallBack());
                        } else {
                            logger.info("Shader " + getName(bundle) + " was succesfully updated!");
                        }
                    } catch (Throwable ex) {
                        logger.warn(ex.getLocalizedMessage());
                    }
                    return true;
                }
            });
        } catch (IOException ex) {
            logger.warn(ex.getLocalizedMessage());
        }
    }

    private String getName(ResourceBundle bundle) {
        String name = "";
        for (int i = 0; i < bundle.getCount(); i++) {
            name += bundle.get(i).getName() + "; ";
        }
        return name;
    }

    @Override
    public Shader getFallBack() {
        try {
            return create(empty);
        } catch (IOException ex) {
            throw new RuntimeException("Could not load fallback shader!");
        }
    }
}
