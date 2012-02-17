/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package darwin.renderer.shader.uniform;

import darwin.renderer.shader.*;
import java.nio.*;

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
