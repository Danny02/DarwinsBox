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
package darwin.renderer.opengl;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.media.opengl.*;
import org.apache.log4j.Logger;

import darwin.renderer.GraphicContext;

/**
 *
 * @author daniel
 */
@Singleton
public class GLClientConstants
{

    private static class Log
    {

        private final static Logger ger = Logger.getLogger(GLClientConstants.class);
    }
    private String glslVersion;
    private int maxSamples;
    private int maxColorAttachments;

    @Inject
    public GLClientConstants(GraphicContext gc)
    {
        Retriver retriver = new Retriver(this);
        gc.getGLWindow().invoke(true, retriver);
    }

    private static class Retriver implements GLRunnable
    {

        GLClientConstants cons;

        public Retriver(GLClientConstants cons)
        {
            this.cons = cons;
        }

        @Override
        public boolean run(GLAutoDrawable drawable)
        {
            GL gl = drawable.getGL();
            int[] container = new int[1];

            cons.glslVersion = retriveGlslVersionString(gl);

            try {
                gl.glGetIntegerv(GL2GL3.GL_MAX_SAMPLES, container, 0);
                cons.maxSamples = container[0];
            } catch (Throwable t) {
                cons.maxSamples = 0;
            }

            try {
                gl.glGetIntegerv(GL2GL3.GL_MAX_COLOR_ATTACHMENTS, container, 0);
                cons.maxColorAttachments = container[0];
            } catch (Throwable t) {
                cons.maxColorAttachments = 0;
            }


            return true;
        }

        private String retriveGlslVersionString(GL gl)
        {

            int v = 120;
            try {
                String ver = gl.glGetString(GL2.GL_SHADING_LANGUAGE_VERSION);
                String s = ver.split(" ")[0];
                s = s.substring(0, 4);
                double d = Double.parseDouble(s);
                d *= 100;
                v = (int) Math.round(d);
            } catch (Throwable ex) {
                Log.ger.warn(ex.getLocalizedMessage());
            }

            return "#version " + v + '\n';
        }
    }

    public String getGlslVersion()
    {
        return glslVersion;
    }

    public int getMaxColorAttachments()
    {
        return maxColorAttachments;
    }

    public int getMaxSamples()
    {
        return maxSamples;
    }
}
