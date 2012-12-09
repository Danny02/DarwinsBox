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
package darwin.resourcehandling.shader;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import darwin.renderer.GraphicContext;
import darwin.renderer.opengl.*;
import darwin.renderer.shader.Shader.ShaderFactory;
import darwin.renderer.shader.*;
import darwin.resourcehandling.dependencies.annotation.InjectBundle;
import darwin.resourcehandling.factory.ResourceFromBundle;
import darwin.resourcehandling.handle.*;
import darwin.resourcehandling.shader.ShaderFile.Builder;
import darwin.util.logging.InjectLogger;

import com.google.common.base.Optional;
import javax.inject.Inject;
import javax.media.opengl.*;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import static darwin.renderer.opengl.ShaderType.*;

/**
 *
 * @author some
 */
public class ShaderLoader implements ResourceFromBundle<Shader> {

    @InjectBundle(files = {"Empty.frag", "Empty.vert"}, prefix = SHADER_PATH_PREFIX)
    private ResourceBundle empty;
    @InjectLogger
    private Logger logger = NOPLogger.NOP_LOGGER;
    public static final String INCLUDE_PREFIX = "#pragma include";
    public static final String SHADER_PATH_PREFIX = "resources/shaders/";
    public static final Path SHADER_PATH = Paths.get(SHADER_PATH_PREFIX);
    private final ShaderObjektFactory soFactory;
    private final GLClientConstants constants;
    private final FileHandlerFactory fileFactory;
    private final ShaderFactory shaderFactory;
    private final GraphicContext gcontext;

    @Inject
    public ShaderLoader(ShaderObjektFactory soFactory, GLClientConstants constants,
                        FileHandlerFactory fileFactory, ShaderFactory shaderFactory, GraphicContext gcontext) {
        this.soFactory = soFactory;
        this.constants = constants;
        this.fileFactory = fileFactory;
        this.shaderFactory = shaderFactory;
        this.gcontext = gcontext;
    }

    @Override
    public Shader create(ResourceBundle bundle) throws IOException {
        Builder builder = new Builder();
        switch (bundle.getCount()) {
            default:
            case 3:
                builder.withGeometrie(getData(bundle.get(2).getStream()));
            case 2:
                builder.withVertex(getData(bundle.get(1).getStream()));
            case 1:
                builder.withFragment(getData(bundle.get(0).getStream()));
        }
        builder.withName(bundle.toString()).withMutations(bundle.getOptions());

        final ShaderFile file = builder.create();
        final Shader shader = shaderFactory.create(file);

        gcontext.invoke(false, new GLRunnable() {
            @Override
            public boolean run(GLAutoDrawable glad) {
                try {
                    ShaderProgramm compiledShader = compileShader(file);
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
    public void update(final ResourceBundle bundle, ResourceHandle changed, final Shader shader) {
        try {
            String d = null;
            ShaderType t = null;
            switch (bundle.getCount()) {
                default:
                case 3:
                    if (bundle.get(2) == changed) {
                        d = getData(bundle.get(2).getStream());
                        t = ShaderType.Geometrie;
                        break;
                    }
                case 2:
                    if (bundle.get(1) == changed) {
                        d = getData(bundle.get(1).getStream());
                        t = ShaderType.Vertex;
                        break;
                    }
                case 1:
                    if (bundle.get(0) == changed) {
                        d = getData(bundle.get(0).getStream());
                        t = ShaderType.Fragment;
                        break;
                    }
            }

            final ShaderType type = t;
            final String data = d;
            gcontext.invoke(false, new GLRunnable() {
                @Override
                public boolean run(GLAutoDrawable glad) {
                    try {
                        GL2GL3 gl = glad.getGL().getGL2GL3();
                        ShaderObjekt so = createSObject(type, data, bundle.getOptions());
                        int po = shader.getProgramm().getPObject();

                        gl.glAttachShader(po, so.getShaderobjekt());
                        gl.glLinkProgram(po);
                        shader.ini(shader.getProgramm());

                        Optional<String> error = shader.getProgramm().verify();
                        if (error.isPresent()) {
                            logger.warn(error.get());
                            shader.ini(getFallBack().getProgramm());
                        } else {
                            logger.info("Shader " + bundle + " was succesfully updated!");
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

    @Override
    public Shader getFallBack() {
        try {
            return create(empty);
        } catch (IOException ex) {
            throw new RuntimeException("Could not load fallback shader!");
        }
    }

    //TODO compile fehler vllcht auffangen, zumindestens im DEV mode?
    public ShaderProgramm compileShader(ShaderFile sfile) {
//        boolean error = false;
//        StringBuilder errorText = new StringBuilder("Shader ");
        String errorMessage = null;
        BuildException exception = null;
        ShaderObjekt fso, vso, gso;
        fso = gso = vso = null;

        try {
            if (sfile.fragment != null) {
                fso = createSObject(Fragment, sfile.fragment, sfile.mutations);
            }
        } catch (BuildException ex) {
            exception = ex;
            errorMessage = "in:Fragment of";
        }
        try {
            if (sfile.vertex != null) {
                vso = createSObject(Vertex, sfile.vertex, sfile.mutations);
            }
        } catch (BuildException ex) {
            exception = ex;
            errorMessage = "in: Vertex of";
        }

        try {
            if (sfile.geometrie != null) {
                gso = createSObject(Geometrie, sfile.geometrie, sfile.mutations);
            }
        } catch (BuildException ex) {
            exception = ex;
            errorMessage = "in: Geometrie of";
        }

        if (exception == null) {
            try {
                return new ShaderProgramBuilder().
                        with(sfile.getAttributs()).
                        with(fso).with(vso).with(gso).
                        link(gcontext);
            } catch (BuildException ex) {
                exception = ex;
                errorMessage = "while linking";
            }
        }
        throw new RuntimeException("Shader " + exception.getErrorType() + " ERROR " + errorMessage
                                   + " {" + sfile.name + "}\n" + exception.getMessage());
    }

    public ShaderObjekt createSObject(ShaderType target, String source,
                                      String... mut) throws BuildException {
        String[] sources = new String[2 + mut.length];
        sources[0] = constants.getGlslVersion();
        for (int i = 0; i < mut.length; ++i) {
            sources[i + 1] = "#define " + mut[i] + '\n';
        }

        sources[mut.length + 1] = source;
        return soFactory.create(target, sources);
    }

    public String getData(InputStream file) {
        if (file == null) {
            return null;
        }
        String out;
        StringBuilder sb = new StringBuilder();
        try {
            Reader fr = new InputStreamReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(INCLUDE_PREFIX)) {
                    String path = line.substring(INCLUDE_PREFIX.length()).trim();
                    InputStream shader = fileFactory.create(SHADER_PATH_PREFIX + path).getStream();
                    if (shader != null) {
                        String src = getData(shader);
                        sb.append(src);
                        try {
                            shader.close();
                        } catch (IOException ex) {
                        }
                    }
                    continue;
                } else if (line.startsWith("#version")) {
                    continue;
                }
                sb.append(line).append('\n');
            }
        } catch (IOException ex) {
            logger.error("Fehler beim laden eines Shader Source Strings: "
                         + ex.getLocalizedMessage());
        }
        out = sb.toString();
        try {
            file.close();
        } catch (IOException ex) {
        }
        return out;
    }
}
