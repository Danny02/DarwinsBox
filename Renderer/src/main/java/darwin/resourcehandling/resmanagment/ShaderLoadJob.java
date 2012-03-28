/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.resourcehandling.resmanagment;

import java.io.IOException;
import java.util.List;
import org.apache.log4j.Logger;

import darwin.renderer.opengl.ShaderProgramm;
import darwin.renderer.shader.Shader;
import darwin.resourcehandling.io.ShaderFile;
import darwin.resourcehandling.io.ShaderUtil;
import darwin.resourcehandling.resmanagment.texture.ShaderDescription;

/**
 *
 * @author dheinrich
 */
public class ShaderLoadJob implements LoadJob<ShaderProgramm>
{
    private static class Log
    {
        public static final Logger ger = Logger.getLogger(ShaderLoadJob.class);
    }
    private final ShaderDescription desc;
    private ShaderFile sfile;
    private List<Shader> scontainer;

//    public ShaderLoadJob(String f, String v, String g) {
//        this(new ShaderDescription(f, v, g));
//    }
    public ShaderLoadJob(ShaderDescription dscr) {
        desc = dscr;
    }

    public void setConList(List<Shader> scontainer) {
        synchronized (scontainer) {
            this.scontainer = scontainer;
        }
    }

    @Override
    public ShaderProgramm load() throws IOException {
        long t = System.currentTimeMillis();
        ShaderProgramm sp = ShaderUtil.compileShader(getSfile());
        if (scontainer != null)
            synchronized (scontainer) {
                for (Shader sc : scontainer)
                    //TODO selber shader wird mehrmals initialisiert
                    sc.ini(sp);
            }
        t = System.currentTimeMillis() - t;
        Log.ger.info("Shader(" + desc + ", [" + getMutantString() + "])  ...loaded("+t+"ms)!");
        return sp;
    }

    private String getMutantString() {
        StringBuilder sb = new StringBuilder();
        int len = desc.flags.length;
        for (int i=0; i<len; ++i) {
            sb.append(desc.flags[i]);
            if(i<len-1)
            sb.append(", ");
        }
        return sb.toString();
    }

    public ShaderFile getSfile() throws IOException {
        if (sfile == null)
            sfile = ShaderUtil.loadShader(desc);
        return sfile;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ShaderLoadJob other = (ShaderLoadJob) obj;
        if (this.desc != other.desc && (this.desc == null || !this.desc.equals(
                other.desc)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.desc != null ? this.desc.hashCode() : 0);
        return hash;
    }
}
