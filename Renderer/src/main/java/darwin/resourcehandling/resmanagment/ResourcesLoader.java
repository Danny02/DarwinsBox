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
package darwin.resourcehandling.resmanagment;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import javax.inject.Inject;
import javax.inject.Singleton;

import lzma.sdk.lzma.Decoder;
import lzma.streams.LzmaInputStream;

import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import com.jogamp.opengl.util.texture.Texture;

import darwin.renderer.geometrie.packed.RenderModel;
import darwin.renderer.geometrie.packed.RenderObjekt;
import darwin.renderer.opengl.ShaderProgramm;
import darwin.renderer.shader.Shader;
import darwin.renderer.shader.Shader.ShaderFactory;
import darwin.resourcehandling.io.ShaderFile;
import darwin.resourcehandling.resmanagment.ROLoadJob.ROJobFactory;
import darwin.resourcehandling.resmanagment.ShaderLoadJob.ShaderJobFactory;
import darwin.resourcehandling.resmanagment.texture.ShaderDescription;
import darwin.resourcehandling.resmanagment.texture.TextureLoadJob;
import darwin.resourcehandling.wrapper.TextureContainer;
import darwin.util.logging.InjectLogger;

/**
 *
 * @author dheinrich
 */
@Singleton
public class ResourcesLoader
{

    @InjectLogger
    private Logger logger = NOPLogger.NOP_LOGGER;
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
    private final ROJobFactory roFactory;
    private final ShaderFactory shaderFactory;
    private final ShaderJobFactory shaderJobFactory;

    @Inject
    public ResourcesLoader(ROJobFactory roFactory, ShaderFactory shaderFactory, ShaderJobFactory shaderJobFactory)
    {
        this.roFactory = roFactory;
        this.shaderFactory = shaderFactory;
        this.shaderJobFactory = shaderJobFactory;
    }

    public Shader getShader(String frag, String vertex, String geo,
            String... mutations)
    {
        return getShader(new ShaderDescription(frag, vertex, geo, mutations));
    }

    public Shader getShader(String name)
    {
        return getShader(new ShaderDescription(name));
    }

    synchronized public Shader getShader(ShaderDescription descr,
            String... mutations)
    {
        ShaderLoadJob job = shaderJobFactory.create(descr.mergeFlags(mutations));
        ShaderFile file = shaderfiles.get(job);
        if (file == null) {
            try {
                file = job.getSfile();
            } catch (IOException ex) {
                logger.error(ex.getLocalizedMessage());
                throw new UnsupportedOperationException(ex);
            }
            shaderfiles.put(job, file);
        }
        Shader shader = shaderFactory.create(file);
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

    synchronized public void getRenderObjekt(RenderObjekt ro, ObjConfig oconf)
    {
        ROLoadJob job = roFactory.create(oconf);
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

    synchronized public TextureContainer getTexture(TextureLoadJob ljob)
    {
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

    synchronized public void workAllJobs()
    {
        try {
            while (!jobs.isEmpty()) {
                loadJob(jobs.remove());
            }
            while (!shadertoset.empty()) {
                shadertoset.pop().load();
            }
        } catch (Throwable ex) {
            logger.error("Unresolved error in resource loading! Error: " + ex.getLocalizedMessage(), ex);
        }
    }

    private void loadJob(LoadJob<?> j)
    {
        try {
            Object r = j.load();
            ressourcen.put(j, r);
            oldjobs.add(j);
        } catch (IOException ex) {
            logger.warn("One resource failed to load: " + ex.getLocalizedMessage());
        }
    }

    synchronized public void reloadRessources()
    {
        while (!oldjobs.isEmpty()) {
            jobs.add(oldjobs.remove());
        }
    }

    public InputStream getRessource(String path) throws IOException
    {
        InputStream is = getStream(path + ".lzma");
        if (is != null) {
            return new LzmaInputStream(is, new Decoder());
        } else {
            is = getStream(path);
            if (is == null) {
                throw new IOException("Resource couldn't be found: " + path);
            }
        }

        return is;
    }

    private InputStream getStream(String path)
    {
        return ResourcesLoader.class.getResourceAsStream('/' + path);
    }
}
