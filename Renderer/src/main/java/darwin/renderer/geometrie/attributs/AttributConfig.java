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
package darwin.renderer.geometrie.attributs;

import darwin.geometrie.data.DataAttribut;
import darwin.renderer.shader.ShaderAttribute;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
class AttributConfig
{

    public final int index, stride, offset, glconst, size;

    AttributConfig(ShaderAttribute sa, DataAttribut dattr)
    {
        this.index = sa.getIndex();
        stride = dattr.stride;
        offset = dattr.offset;
        glconst = sa.element.getDataType().getGLConst();
        size = sa.element.getVectorType().getElementCount();
    }
}
