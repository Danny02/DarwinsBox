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
package darwin.renderer.shader.uniform;

import java.nio.FloatBuffer;

import darwin.renderer.shader.ShaderUniform;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class FloatSetter implements UniformSetter{
    private final ShaderUniform uni;
    private final FloatBuffer value;

    public FloatSetter(ShaderUniform uni, float... value) {
        assert uni != null && value != null;
        this.uni = uni;
        this.value = FloatBuffer.wrap(value);
    }

    public void set() {
        uni.setData(value);
    }
}
