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
 * You should have received a clone of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.inject.Inject;
import javax.media.opengl.*;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import darwin.renderer.dependencies.MemoryInfoProvider;
import darwin.renderer.geometrie.packed.*;
import darwin.renderer.shader.*;
import darwin.renderer.util.memory.*;
import darwin.resourcehandling.resmanagment.ResourcesLoader;
import darwin.util.logging.InjectLogger;
import darwin.util.math.base.vector.*;
import darwin.util.math.composits.*;
import darwin.util.math.util.*;

/**
 * Basic scene manager
 * <p/>
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class BasicScene implements GLEventListener
{

    @InjectLogger
    private Logger logger = NOPLogger.NOP_LOGGER;
    protected final GraphicContext gc;
    protected final ResourcesLoader loader;
//    private final List<GenListener<Dimension>> dimlistener =
//                                               new LinkedList<GenListener<Dimension>>();
    private final Queue<Renderable> robjekts = new ConcurrentLinkedQueue<>();
    private final Set<Shader> shaders = new HashSet<>();
    private final List<ShaderUniform> half, lightdir, animated;
    private final Queue<RenderObjekt> needini = new ConcurrentLinkedQueue<>();
    private final MatrixCache matrices;
    //test
    private PerformanceView info;
    private MemoryInfo meminfo;

    @Inject
    public BasicScene(GraphicContext gc, ResourcesLoader loader)
    {
        this.gc = gc;
        this.loader = loader;
        matrices = new MatrixCache(new ProjectionMatrix());
        half = new LinkedList<>();
        lightdir = new LinkedList<>();
        animated = new LinkedList<>();
    }

    protected void customRender()
    {
        renderObjects();
    }

    protected void renderObjects()
    {
        Iterator<RenderObjekt> iter = needini.iterator();
        while (iter.hasNext()) {
            RenderModel[] rms = iter.next().getModels();
            if (rms != null) {
                iter.remove();
                for (RenderModel rm : rms) {
                    addShader(rm.getShader());
                }
            }
        }
        for (Renderable ra : robjekts) {
            ra.render();
        }
    }

    @Override
    public void init(GLAutoDrawable drawable)
    {
        //TODO Debug und Trace GL einbauen
        GL gl = gc.getGL();
        logger.info("INIT GL IS: " + gl.getClass().getName());

        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        // Enable VSync
        gl.setSwapInterval(1);
        // Setup the drawing area and shading mode
        gl.glClearColor(0.0f, 1.0f, 1.0f, 0f);

        meminfo = new MemoryInfoProvider(gc).get();
    }

    @Override
    public final void display(GLAutoDrawable drawable)
    {
        if (!drawable.getContext().isCurrent()) {
            return;
        }
        loader.workAllJobs();
        try {
            customRender();
        } catch (Throwable ex) {
            logger.error("Error in custom code!", ex);
        }

        GLAnimatorControl ani = drawable.getAnimator();
//TODO GameTime updates einbaun
        logger.trace(String.format("%d Objects took %dms  to render; fps:%f",
                robjekts.size(), ani.getLastFPSPeriod(), ani.getLastFPS()));
        if (info != null) {
            info.setFPS(ani.getTotalFPS());
        }

        float delta = ani.getLastFPSPeriod();
        for (ShaderUniform su : animated) {
            su.setData(delta);
        }

        if (info != null) {
            info.setMemInfo(meminfo);
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
            int height)
    {
        final float ratio = (float) height / Math.min(1, width);
        matrices.getProjektion().perspective(50, ratio, 0.001, 1000);
        matrices.fireChange(MatType.PROJECTION);
    }

    @Override
    public void dispose(GLAutoDrawable drawable)
    {
        robjekts.clear();
        shaders.clear();
    }

    public void addSceneObject(RenderObjekt ro)
    {
        needini.add(ro);
        robjekts.add(new RenderWrapper(ro, matrices));
    }

    public void addSceneObject(Shaded r)
    {
        robjekts.add(r);
        addShader(r.getShader());
    }

    public void addShader(Shader shader)
    {
        if (shaders.add(shader)) {
            ShaderUniform h = shader.getUniform("halfvector");
            if (h != null) {
                half.add(h);
            }
            ShaderUniform l = shader.getUniform("light_pos");
            if (l != null) {
                lightdir.add(l);
            }
            ShaderUniform a = shader.getUniform("time_delta");
            if (a != null) {
                animated.add(a);
            }
            iniShader(shader);
            matrices.addListener(shader);
        }
    }

    protected void iniShader(Shader shader)
    {
    }

    public void removeSceneObject(Renderable wo)
    {
        robjekts.remove(wo);
    }

    public void removeSceneObject(RenderObjekt ro)
    {
        robjekts.remove(new RenderWrapper(ro, null));
    }

    public void setLigthDir(ImmutableVector<Vector3> lightdir)
    {
        getMatrices().setLight(lightdir.clone().mul(-1));
        Vector3 light = matrices.getView().fastMult(lightdir.clone());
        light.normalize();

        Vector3 halfvector = new Vector3(0, 0, 1).add(light).normalize();

        setLightUniforms(light, halfvector);
    }

    protected void setLightUniforms(Vector3 ldir, Vector3 half)
    {
        for (ShaderUniform su : this.lightdir) {
            su.setData(ldir.getCoords());
        }

        for (ShaderUniform su : this.half) {
            su.setData(half.getCoords());
        }
    }

    public void setViewMatrix(ViewMatrix mv)
    {
        matrices.setView(mv);
    }

    public void viewChanged()
    {
        matrices.fireChange(MatType.VIEW);
    }

    public MatrixCache getMatrices()
    {
        return matrices;
    }

    protected Iterator<Shader> getShaders()
    {
        return shaders.iterator();
    }

    public void setInfoDisplay(PerformanceView info)
    {
        this.info = info;
    }
}
