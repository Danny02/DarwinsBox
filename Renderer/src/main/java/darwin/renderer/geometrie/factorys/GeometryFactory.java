/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package darwin.renderer.geometrie.factorys;

import darwin.renderer.geometrie.data.RenderMesh;
import darwin.renderer.shader.Shader;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public interface GeometryFactory {
    public RenderMesh buildRenderable(Shader shader);
}
