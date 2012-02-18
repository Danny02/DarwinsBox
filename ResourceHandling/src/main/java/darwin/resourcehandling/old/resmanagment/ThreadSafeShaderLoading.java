/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package darwin.resourcehandling.old.resmanagment;

import darwin.renderer.opengl.ShaderProgramm;
import darwin.renderer.shader.Shader;


/**
 *
 * @author dheinrich
 */
public class ThreadSafeShaderLoading{
    private Shader sc;
    private ShaderProgramm sp;

    public ThreadSafeShaderLoading(Shader sc, ShaderProgramm sp) {
        this.sc = sc;
        this.sp = sp;
    }

    public void load() {
        sc.ini(sp);
    }

}
