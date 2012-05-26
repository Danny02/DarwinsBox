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

/**
 *
 * @author daniel
 */
/**
 * The Access Type tells OpenGL how often you intend to modify the data. These
 * types are only optimization hints for the driver.
 */
public enum Type
{
    //The ordering of the elements must not be changed

    /**
     * You will modify the data once, then use it once, and repeat this process
     * many times.
     */
    STREAM, /**
     * You will specify the data only once, then use it many times without
     * modifying it.
     */
    STATIC, /**
     * You will specify or modify the data repeatedly, and use it repeatedly
     * after each time you do this.
     */
    DYNAMIC;
    final int glconst = 35040 + 4 * ordinal();
}
