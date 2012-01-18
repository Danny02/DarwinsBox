/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.renderer.geometrie.attributs;

import de.dheinrich.darwin.renderer.opengl.BufferObject;
import javax.media.opengl.GL2GL3;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
class BufferConfigs
{
    private final BufferObject buffer;
    private final AttributConfig[] configs;

    public BufferConfigs(BufferObject buffer, AttributConfig[] configs) {
        this.buffer = buffer;
        this.configs = configs;
        assert buffer != null && configs != null :
                "Der Buffer sowie das Attribut Array darf nicht null sein";
    }

    public void prepare(GL2GL3 gl) {
        buffer.bind();
        for (AttributConfig ac : configs) {
            gl.glEnableVertexAttribArray(ac.index);
            gl.glVertexAttribPointer(ac.index, ac.size, ac.glconst,
                                     false, ac.stride, ac.offset);
        }
        buffer.disable();
    }

    public void disable(GL2GL3 gl) {
        for (AttributConfig ac : configs)
            gl.glDisableVertexAttribArray(ac.index);
    }
}
