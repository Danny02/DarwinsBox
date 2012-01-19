/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.dheinrich.darwin.renderer.geometrie.attributs;

import de.dheinrich.darwin.renderer.geometrie.data.*;
import de.dheinrich.darwin.renderer.shader.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
class AttributConfig {
    public final int index, stride, offset, glconst, size;

    public AttributConfig(ShaderAttribute sa, DataAttribut dattr) {
        this.index = sa.getIndex();
        stride = dattr.stride;
        offset = dattr.offset;
        glconst = sa.element.gltype.datatype.getGLConst();
        size = sa.element.gltype.size;
    }
}
