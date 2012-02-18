/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer.shader;

import darwin.renderer.opengl.Element;
import darwin.renderer.opengl.ShaderProgramm;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public interface ShaderElement
{
    public Element getElement();
    public String getName();
    public int getIndex();
    public void ini(ShaderProgramm prog);
}