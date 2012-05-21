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
import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import darwin.renderer.opengl.*;
import darwin.renderer.shader.BuildException;
import darwin.resourcehandling.resmanagment.ResourcesLoader;
import darwin.resourcehandling.resmanagment.texture.ShaderDescription;
import darwin.util.logging.InjectLogger;

import static darwin.renderer.opengl.ShaderType.*;

/**
 *
 * @author Daniel Heinrich
 */
@Singleton
public class ShaderUtil
{

    @InjectLogger
    private Logger logger = NOPLogger.NOP_LOGGER;
    private static final String includePrefix = "#pragma include";
    private static final String RES_PATH = "resources/shaders/";
    private final ShaderObjektFactory soFactory;
    private final GLClientConstants constants;
    private final ShaderProgrammFactory spFactory;
    private final ResourcesLoader resourceLoader;

    @Inject
    public ShaderUtil(ShaderObjektFactory soFactory, GLClientConstants constants,
            ShaderProgrammFactory spFactory, ResourcesLoader resourceLoader)
    {
        this.soFactory = soFactory;
        this.constants = constants;
        this.spFactory = spFactory;
        this.resourceLoader = resourceLoader;
    }

    //TODO compile fehler vllcht auffangen, zumindestens im DEV mode?
    public ShaderProgramm compileShader(ShaderFile sfile)
    {
        ShaderObjekt fso, vso, gso;
        try {
            fso = createSObject(Fragment, sfile.fragment, sfile.mutations);
            vso = createSObject(Vertex, sfile.vertex, sfile.mutations);
            gso = createSObject(Geometrie, sfile.geometrie, sfile.mutations);
            return spFactory.create(sfile.getAttributs(), fso, vso, gso);
        } catch (BuildException ex) {
            //TODO Vllcht im Debug Modus einen Dummy shader generieren aus den gegebenen infos
            logger.error("Shader " + ex.getErrorType() + " ERROR in : "
                    + sfile.name + "\n" + ex.getMessage());
            throw new Error("Shutting down!");
        }
    }

    private ShaderObjekt createSObject(ShaderType target, String source,
            String... mut) throws BuildException
    {
        if (source == null) {
            return null;
        }
        int len = mut != null ? mut.length : 0;
        String[] sources = new String[2 + len];
        sources[0] = constants.getGlslVersion();
        for (int i = 0; i < len; ++i) {
            sources[i + 1] = "#define " + mut[i] + '\n';
        }

        sources[len + 1] = source;

        return soFactory.create(target, sources);
    }

    public ShaderFile loadShader(ShaderDescription dscr) throws IOException
    {
        return loadShader(dscr.f, dscr.v, dscr.g, dscr.flags);
    }

    /**
     * Erstellt eines ShaderProgramm Object. <br>
     * <p/>
     * @param gl  <br> Der GL Context in dem das Programm erstellt werden soll.
     *            <br>
     * @param vs  <br> Pfad der auf eine Vertex ShaderProgramm Datei zeigt
     *            (relativ zu "./resources/Shaders/"). <br>
     * @param fs  <br> Pfad der auf eine Fragment ShaderProgramm Datei zeigt
     *            (relativ zu "./resources/Shaders/"). <br>
     * @param uni <br> Eine Liste von Uniform variable der ShaderProgramm deren
     *            positionen abgefragt werden sollen.
     */
    public ShaderFile loadShader(String fs, String vs, String gs,
            String... ms) throws IOException
    {
        InputStream fragis = null, vertis = null, geois = null;
        if (fs != null) {
            fragis = resourceLoader.getRessource(RES_PATH + fs);
        }
        if (vs != null) {
            vertis = resourceLoader.getRessource(RES_PATH + vs);
        }
        if (gs != null) {
            geois = resourceLoader.getRessource(RES_PATH + gs);
        }

        String name = vs + "\t" + fs + "\t" + gs + " - " + Arrays.toString(ms);
        ShaderFile ret = loadShader(name, fragis, vertis, geois, ms);

        if (vertis != null) {
            try {
                vertis.close();
            } catch (IOException ex) {
            }
        }
        if (fragis != null) {
            try {
                fragis.close();
            } catch (IOException ex) {
            }
        }
        if (geois != null) {
            try {
                geois.close();
            } catch (IOException ex) {
            }
        }
        return ret;
    }

    private ShaderFile loadShader(String name, InputStream fs,
            InputStream vs, InputStream gs,
            String... mutations)
    {
        //TODO ueberlegen defines nicht der src hinzuzufügen
        //sondern als weiteren src string uebergeben
        String f = getData(fs);
        String v = getData(vs);
        String g = getData(gs);
        return new ShaderFile(name, f, v, g, mutations);
    }

    private String getData(InputStream file)
    {
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
                if (line.startsWith(includePrefix)) {
                    String path = line.substring(includePrefix.length()).trim();
                    InputStream shader = resourceLoader.getRessource(RES_PATH + path);
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
        return out;
    }
}
