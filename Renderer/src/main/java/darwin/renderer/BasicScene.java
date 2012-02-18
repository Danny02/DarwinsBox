/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.media.opengl.*;
import org.apache.log4j.Logger;

import darwin.renderer.geometrie.packed.*;
import darwin.renderer.shader.Shader;
import darwin.renderer.shader.ShaderUniform;
import darwin.renderer.util.memory.MemoryInfo;
import darwin.renderer.util.memory.PerformanceView;
import darwin.util.math.base.Matrix;
import darwin.util.math.base.Vec3;
import darwin.util.math.composits.ProjectionMatrix;
import darwin.util.math.composits.ViewMatrix;
import darwin.util.math.util.MatType;
import darwin.util.math.util.MatrixCache;

import static darwin.renderer.GraphicContext.*;
import static darwin.resourcehandling.resmanagment.RessourcesLoader.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public abstract class BasicScene implements GLEventListener
{

    private static class Log
    {

        private static Logger ger = Logger.getLogger(BasicScene.class);
    }
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

    public BasicScene()
    {
        matrices = new MatrixCache(new ProjectionMatrix());
        half = new LinkedList<>();
        lightdir = new LinkedList<>();
        animated = new LinkedList<>();
    }

    protected abstract void customRender();

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
        GL gl = getGL();
        Log.ger.info("INIT GL IS: " + gl.getClass().getName());

        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        // Enable VSync
        gl.setSwapInterval(1);
        // Setup the drawing area and shading mode
        gl.glClearColor(0.0f, 1.0f, 1.0f, 0f);

        meminfo = MemoryInfo.INSTANCE;
    }

    @Override
    public final void display(GLAutoDrawable drawable)
    {
        RESOURCES.workAllJobs();
        try {
            customRender();
        } catch (Throwable ex) {
            Log.ger.fatal("Error in custom code!", ex);
        }

        GLAnimatorControl ani = drawable.getAnimator();
//TODO GameTime updates einbaun
        Log.ger.trace(String.format("%d Objects took %dms  to render; fps:$f",
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

    public void setLigthDir(Vec3 lightdir)
    {
        getMatrices().setLight(lightdir.mult(-1));
        Matrix view3 = matrices.getView().getMinor(3, 3);
        Vec3 light = new Vec3(view3.mult(lightdir));
        light.normalize(light);

        Vec3 halfvector = new Vec3(0, 0, 1);
        halfvector.add(light, halfvector);
        halfvector.normalize(halfvector);

        setLightUniforms(light, halfvector);
    }

    protected void setLightUniforms(Vec3 ldir, Vec3 half)
    {
        for (ShaderUniform su : this.lightdir) {
            su.setData(ldir.getCoordsF());
        }

        for (ShaderUniform su : this.half) {
            su.setData(half.getCoordsF());
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

    public boolean append2Initiation(RenderObjekt e)
    {
        return needini.add(e);
    }

    public void setInfoDisplay(PerformanceView info)
    {
        this.info = info;
    }
}