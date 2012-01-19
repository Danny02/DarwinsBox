/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.ressourcen.resmanagment;

import LZMA.*;
import com.jogamp.opengl.util.texture.*;
import de.dheinrich.darwin.renderer.geometrie.packed.*;
import de.dheinrich.darwin.renderer.opengl.*;
import de.dheinrich.darwin.renderer.shader.*;
import de.dheinrich.darwin.ressourcen.io.*;
import de.dheinrich.darwin.ressourcen.resmanagment.texture.*;
import de.dheinrich.darwin.ressourcen.wrapper.*;
import java.io.*;
import java.util.*;
import org.apache.log4j.*;

/**
 *
 * @author dheinrich
 */
public class RessourcesLoader {

    private static class Log {

        private static Logger ger = Logger.getLogger(RessourcesLoader.class);
    }
    public static final RessourcesLoader RESOURCES = new RessourcesLoader();
    private final Queue<LoadJob<?>> jobs = new LinkedList<>();
    private final Queue<LoadJob<?>> oldjobs = new LinkedList<>();
    private final HashMap<LoadJob<?>, Object> ressourcen = new HashMap<>();
    //shader stuff
    private final HashMap<ShaderLoadJob, List<Shader>> shadermap = new HashMap<>();
    private final HashMap<ShaderLoadJob, ShaderFile> shaderfiles = new HashMap<>();
    private Stack<ThreadSafeShaderLoading> shadertoset = new Stack<>();
    //texture stuff
    private final HashMap<TextureLoadJob, TextureContainer> texturemap =
            new HashMap<>();
    //Mesh stuff
    private final HashMap<ROLoadJob, List<RenderObjekt>> meshmap = new HashMap<>();

    private RessourcesLoader() {
    }

    public Shader getShader(String frag, String vertex, String geo,
            String... mutations) {
        return getShader(new ShaderDescription(frag, vertex, geo, mutations));
    }

    public Shader getShader(String name) {
        return getShader(new ShaderDescription(name));
    }

    synchronized public Shader getShader(ShaderDescription descr,
            String... mutations) {
        ShaderLoadJob job = new ShaderLoadJob(descr.mergeFlags(mutations));
        ShaderFile file = shaderfiles.get(job);
        if (file == null) {
            file = job.getSfile();
            shaderfiles.put(job, file);
        }
        Shader shader = new Shader(file);
        ShaderProgramm prog = (ShaderProgramm) ressourcen.get(job);
        if (prog == null) {
            List<Shader> l = shadermap.get(job);
            if (l == null) {
                l = new LinkedList<>();
                shadermap.put(job, l);
                job.setConList(l);
                jobs.add(job);
            }
            l.add(shader);
        } else {
            shadermap.get(job).add(shader);
            shadertoset.add(new ThreadSafeShaderLoading(shader, prog));
        }
        return shader;
    }

    synchronized public void getRenderObjekt(RenderObjekt ro, ObjConfig oconf) {
        ROLoadJob job = new ROLoadJob(oconf);
        RenderModel[] models = (RenderModel[]) ressourcen.get(job);
        List<RenderObjekt> l = meshmap.get(job);
        if (models == null) {
            if (l == null) {
                l = new LinkedList<>();
                meshmap.put(job, l);
                job.setConList(l);
                jobs.add(job);
            }
            l.add(ro);
        } else {
            l.add(ro);
            ro.setModels(models);
        }
    }

    synchronized public TextureContainer getTexture(TextureLoadJob ljob) {
        Texture res = (Texture) ressourcen.get(ljob);
        if (res == null) {
            TextureContainer tc = texturemap.get(ljob);
            if (tc == null) {
                tc = new TextureContainer();
                texturemap.put(ljob, tc);
                ljob.setCon(tc);
                jobs.add(ljob);
            }
            return tc;
        } else {
            return texturemap.get(ljob);
        }
    }

    synchronized public void workAllJobs() {
        try {
            while (!jobs.isEmpty()) {
                loadJob(jobs.remove());
            }
            while (!shadertoset.empty()) {
                shadertoset.pop().load();
            }
        } catch (Throwable ex) {
            Log.ger.fatal("Unresolved error in resource loading!", ex);
        }
    }

    private void loadJob(LoadJob<?> j) {
        ressourcen.put(j, j.load());
        oldjobs.add(j);
    }

    synchronized public void reloadRessources() {
        while (!oldjobs.isEmpty()) {
            jobs.add(oldjobs.remove());
        }
    }

    public static InputStream getRessource(String path) {
        InputStream is = getStream(path + ".lzma");
        if (is != null) {
            return new LzmaInputStream(is);
        }

        return getStream(path);
    }

    private static InputStream getStream(String path) {
        return RESOURCES.getClass().getResourceAsStream('/' + path);
    }
}
