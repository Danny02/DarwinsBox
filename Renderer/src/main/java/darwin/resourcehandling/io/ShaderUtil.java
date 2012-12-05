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
package darwin.resourcehandling.io;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import darwin.renderer.GraphicContext;
import darwin.renderer.opengl.*;
import darwin.renderer.shader.BuildException;
import darwin.renderer.shader.ShaderProgramBuilder;
import darwin.resourcehandling.handle.FileHandlerFactory;
import darwin.util.logging.InjectLogger;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import static darwin.renderer.opengl.ShaderType.*;

/**
 *
 * @author Daniel Heinrich
 */
@Singleton
public class ShaderUtil {

    @InjectLogger
    private Logger logger = NOPLogger.NOP_LOGGER;
    public static final String INCLUDE_PREFIX = "#pragma include";
    public static final String SHADER_PATH_PREFIX = "resources/shaders/";
    public static final Path SHADER_PATH = Paths.get(ShaderUtil.SHADER_PATH_PREFIX);
    private final ShaderObjektFactory soFactory;
    private final GLClientConstants constants;
    private final GraphicContext gc;
    private final FileHandlerFactory fileFactory;

    @Inject
    public ShaderUtil(ShaderObjektFactory soFactory, GLClientConstants constants, GraphicContext gc, FileHandlerFactory fileFactory) {
        this.soFactory = soFactory;
        this.constants = constants;
        this.gc = gc;
        this.fileFactory = fileFactory;
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
                        link(gc);
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
