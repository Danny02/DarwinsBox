/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.dheinrich.darwin.ressourcen.resmanagment;

import de.dheinrich.darwin.renderer.shader.Shader;
import de.dheinrich.darwin.renderer.opengl.ShaderProgramm;


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
