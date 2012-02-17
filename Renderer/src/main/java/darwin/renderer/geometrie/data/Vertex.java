/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer.geometrie.data;

import darwin.renderer.opengl.*;
import java.nio.*;

/**
 * Proxy Klasse um ein einzelens Vertex aus einem VertexBuffer editieren zu k�nnen
 * @author Daniel Heinrich
 */
public final class Vertex
{
    private final VertexBuffer vb;
    public final int ind;

    Vertex(VertexBuffer vb, int ind) {
        this.vb = vb;
        this.ind = ind;
    }

    public void setAttribute(Element ele, Number... values){
        DataAttribut al = vb.layout.getAttribut(ele);
        assert al != null : "This Attribute doesn't exist in the VertexBuffer";
        assert (values.length <= ele.gltype.size) :
                "The Attribute demands " + ele.gltype.size + " values not " + values.length + "!";

        int id = ind * al.stride + al.offset;
        ele.gltype.datatype.put(vb.buffer, id, values);
    }

    public Number[] getAttribute(Element ele) {
        Number[] values = new Number[ele.gltype.size];

        ByteBuffer bb = vb.buffer;
        DataAttribut al = vb.layout.getAttribut(ele);
        int id = al.offset + ind * vb.layout.getBytesize();
        ele.gltype.datatype.get(bb, id, values);

        return values;
    }
}
