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
package darwin.renderer.opengl.buffer;

import javax.media.opengl.*;

/**
 *
 * @author daniel
 */
// <editor-fold defaultstate="collapsed" desc="OpenGL Enums">
public enum Target
{

    ARRAY(GL.GL_ARRAY_BUFFER),
    ELEMENT_ARRAY(GL.GL_ELEMENT_ARRAY_BUFFER),
    PIXEL_PACK(GL2GL3.GL_PIXEL_PACK_BUFFER),
    PIXEL_UNPACK(GL2GL3.GL_PIXEL_UNPACK_BUFFER);
    final int glvalue;

    private Target(int glvalue)
    {
        this.glvalue = glvalue;
    }
}
