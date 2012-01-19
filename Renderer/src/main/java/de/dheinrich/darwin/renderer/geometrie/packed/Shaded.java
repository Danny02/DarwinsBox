/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.dheinrich.darwin.renderer.geometrie.packed;

import de.dheinrich.darwin.renderer.shader.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public interface Shaded extends Renderable {
    public Shader getShader();
}
