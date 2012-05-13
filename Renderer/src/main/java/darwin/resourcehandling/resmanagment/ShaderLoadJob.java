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

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import darwin.renderer.opengl.ShaderProgramm;
import darwin.renderer.shader.Shader;
import darwin.resourcehandling.io.ShaderFile;
import darwin.resourcehandling.io.ShaderUtil;
import darwin.resourcehandling.resmanagment.texture.ShaderDescription;
import darwin.util.logging.InjectLogger;

/**
 *
 * @author dheinrich
 */
public class ShaderLoadJob implements LoadJob<ShaderProgramm>
{

    public interface ShaderJobFactory
    {

        public ShaderLoadJob create(ShaderDescription description);
    }
    @InjectLogger
    private Logger logger = NOPLogger.NOP_LOGGER;
    private final ShaderUtil util;
    private final ShaderDescription desc;
    private ShaderFile sfile;
    private List<Shader> scontainer;

//    public ShaderLoadJob(String f, String v, String g) {
//        this(new ShaderDescription(f, v, g));
//    }
    @AssistedInject
    public ShaderLoadJob(ShaderUtil util, @Assisted ShaderDescription dscr)
    {
        this.util = util;
        desc = dscr;
    }

    public void setConList(List<Shader> scontainer)
    {
        synchronized (scontainer) {
            this.scontainer = scontainer;
        }
    }

    @Override
    public ShaderProgramm load() throws IOException
    {
        long t = System.currentTimeMillis();
        ShaderProgramm sp = util.compileShader(getSfile());
        if (scontainer != null) {
            synchronized (scontainer) {
                for (Shader sc : scontainer) //TODO selber shader wird mehrmals initialisiert
                {
                    sc.ini(sp);
                }
            }
        }
        t = System.currentTimeMillis() - t;
        logger.info("Shader(" + desc + ", [" + getMutantString() + "])  ...loaded(" + t + "ms)!");
        return sp;
    }

    private String getMutantString()
    {
        StringBuilder sb = new StringBuilder();
        int len = desc.flags.length;
        for (int i = 0; i < len; ++i) {
            if (desc.flags[i] != null) {
                sb.append(desc.flags[i]);
            }
            if (i < len - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public ShaderFile getSfile() throws IOException
    {
        if (sfile == null) {
            sfile = util.loadShader(desc);
        }
        return sfile;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ShaderLoadJob other = (ShaderLoadJob) obj;
        if (this.desc != other.desc && (this.desc == null || !this.desc.equals(
                other.desc))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 97 * hash + (this.desc != null ? this.desc.hashCode() : 0);
        return hash;
    }
}
