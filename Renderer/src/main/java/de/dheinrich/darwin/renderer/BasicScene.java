/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.renderer;

import de.dheinrich.darwin.renderer.util.memory.PerformanceView;
import de.dheinrich.darwin.util.math.util.MatType;
import javax.media.opengl.GL2;
import de.dheinrich.darwin.renderer.geometrie.packed.Renderable;
import de.dheinrich.darwin.renderer.shader.Shader;
import de.dheinrich.darwin.renderer.shader.ShaderUniform;
import de.dheinrich.darwin.renderer.geometrie.packed.RenderModel;
import de.dheinrich.darwin.renderer.geometrie.packed.RenderObjekt;
import de.dheinrich.darwin.renderer.geometrie.packed.RenderWrapper;
import de.dheinrich.darwin.renderer.geometrie.packed.Shaded;
import de.dheinrich.darwin.util.math.util.MatrixCache;
import de.dheinrich.darwin.renderer.util.memory.MemoryInfo;
import de.dheinrich.darwin.util.math.base.Matrix;
import de.dheinrich.darwin.util.math.composits.ProjectionMatrix;
import de.dheinrich.darwin.util.math.base.Vec3;
import de.dheinrich.darwin.util.math.composits.ViewMatrix;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.media.opengl.DebugGL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import org.apache.log4j.Logger;
import static de.dheinrich.darwin.ressourcen.resmanagment.RessourcesLoader.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public abstract class BasicScene implements GLEventListener {

    private static class Log {

        private static Logger ger = Logger.getLogger(BasicScene.class);
    }
    private GL2GL3 gl;
//    private final List<GenListener<Dimension>> dimlistener =
//                                               new LinkedList<GenListener<Dimension>>();
    private final Queue<Renderable> robjekts = new ConcurrentLinkedQueue<>();
    private final Set<Shader> shaders = new HashSet<>();
    private final List<ShaderUniform> half, lightdir, animated;
    private final Queue<RenderObjekt> needini = new ConcurrentLinkedQueue<>();
    private final MatrixCache matrices;
    private long time, frametime, starttime;
    //test
    private PerformanceView info;
    private MemoryInfo meminfo;

    public BasicScene() {
        matrices = new MatrixCache(new ProjectionMatrix());
        half = new LinkedList<>();
        lightdir = new LinkedList<>();
        animated = new LinkedList<>();
    }

    protected abstract void customRender();

    protected void renderObjects() {
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
    public void init(GLAutoDrawable drawable) {

//        gl = drawable.getGL().getGL2();
//        gl = (GL2GL3) GLProxy.newInstance(gl);
        drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
        gl = drawable.getGL().getGL2GL3();

        Log.ger.info("INIT GL IS: " + gl.getClass().getName());

        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        // Enable VSync
        gl.setSwapInterval(1);
        // Setup the drawing area and shading mode
        gl.glClearColor(0.0f, 1.0f, 1.0f, 0f);
//        gl.glClearColor(1.0f, 0.0f, 1.0f, 0f);

        meminfo = MemoryInfo.INSTANCE;
    }

    @Override
    public final void display(GLAutoDrawable drawable) {

        RESOURCES.workAllJobs();
        try {
            customRender();
        } catch (Throwable ex) {
            Log.ger.fatal("Error in custom code!", ex);
        }

        if (time != 0) {
            long t = new Date().getTime();
            frametime = t - time;
            double fts = frametime / 1000.;
            if (frametime > 0) {
                Log.ger.trace(String.format("%d Objects took %dms  to render; fps:$f",
                        robjekts.size(), frametime, 1 / fts));
            }
            if (info != null) {
                info.setFPS(1 / fts);
            }
        }

        time = new Date().getTime();
        if (starttime == 0) {
            starttime = time;
        }

        float delta = time - starttime;
        for (ShaderUniform su : animated) {
            su.setData(delta);
        }

        if (info != null) {
            info.setMemInfo(meminfo);
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
            int height) {
        final float ratio = (float) height / Math.min(1, width);
        matrices.getProjektion().perspective(50, ratio, 0.001, 1000);
        matrices.fireChange(MatType.PROJECTION);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        robjekts.clear();
        shaders.clear();
    }

    public void addSceneObject(RenderObjekt ro) {
        needini.add(ro);
        robjekts.add(new RenderWrapper(ro, matrices));
    }

    public void addSceneObject(Shaded r) {
        robjekts.add(r);
        addShader(r.getShader());
    }

    public void addShader(Shader shader) {
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

    protected void iniShader(Shader shader) {
    }

    public void removeSceneObject(Renderable wo) {
        robjekts.remove(wo);
    }

    public void removeSceneObject(RenderObjekt ro) {
        robjekts.remove(new RenderWrapper(ro, null));
    }

    public void setLigthDir(Vec3 lightdir) {
        getMatrices().setLight(lightdir.mult(-1));
        Matrix view3 = matrices.getView().getMinor(3, 3);
        Vec3 light = new Vec3(view3.mult(lightdir));
        light.normalize(light);

        Vec3 halfvector = new Vec3(0, 0, 1);
        halfvector.add(light, halfvector);
        halfvector.normalize(halfvector);

        setLightUniforms(light, halfvector);
    }

    protected void setLightUniforms(Vec3 ldir, Vec3 half) {
        for (ShaderUniform su : this.lightdir) {
            su.setData(ldir.getCoordsF());
        }

        for (ShaderUniform su : this.half) {
            su.setData(half.getCoordsF());
        }
    }

    public void setViewMatrix(ViewMatrix mv) {
        matrices.setView(mv);
    }

    public void viewChanged() {
        matrices.fireChange(MatType.VIEW);
    }

    public MatrixCache getMatrices() {
        return matrices;
    }

    protected Iterator<Shader> getShaders() {
        return shaders.iterator();
    }

    public boolean append2Initiation(RenderObjekt e) {
        return needini.add(e);
    }

    public long getFrametime() {
        return frametime;
    }

    protected GL2GL3 getGl() {
        return gl;
    }

    public void setInfoDisplay(PerformanceView info) {
        this.info = info;
    }
}
