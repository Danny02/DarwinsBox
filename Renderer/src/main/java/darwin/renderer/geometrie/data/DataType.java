/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer.geometrie.data;

import com.jogamp.opengl.util.GLBuffers;
import java.nio.ByteBuffer;
import javax.media.opengl.*;

/**
 * Definiert DatenTypen die in einem Vertex Attribut Array genutzt werden
 * k�nnen.
 * <p/>
 * @author Daniel Heinrich
 */
public enum DataType
{

    SHORT(GL.GL_SHORT, GLBuffers.SIZEOF_SHORT)
    {

        public void put(ByteBuffer buf, int id, Number[] values)
        {
            for (int i = 0; i < values.length; i++) {
                buf.putShort(id + i*byteSize, values[i].shortValue());
            }
        }

        public void get(ByteBuffer buf, int id, Number[] values)
        {
            for (int i = 0; i < values.length; i++) {
                values[i] = buf.getShort(id + i*byteSize);
            }
        }

        public Number[] mul(double mul, Number[] a)
        {
            Short[] c = new Short[a.length];
            for (int i = 0; i < a.length; ++i) {
                c[i] = (short) (a[i].shortValue() * mul);
            }
            return c;
        }

        public Number[] add(Number[] a, Number[] b)
        {
            Short[] c = new Short[a.length];
            for (int i = 0; i < a.length; ++i) {
                c[i] = (short)(a[i].shortValue() + b[i].shortValue());
            }
            return c;
        }
    },
    INT(GL2ES2.GL_INT, GLBuffers.SIZEOF_INT)
    {
        public void put(ByteBuffer buf, int id, Number[] values)
        {
            for (int i = 0; i < values.length; i++) {
                buf.putInt(id + i*byteSize, values[i].intValue());
            }
        }

        public void get(ByteBuffer buf, int id, Number[] values)
        {
            for (int i = 0; i < values.length; i++) {
                values[i] = buf.getInt(id + i*byteSize);
            }
        }

        public Number[] mul(double mul, Number[] a)
        {
            Integer[] c = new Integer[a.length];
            for (int i = 0; i < a.length; ++i) {
                c[i] = (int) (a[i].intValue() * mul);
            }
            return c;
        }

        public Number[] add(Number[] a, Number[] b)
        {
            Integer[] c = new Integer[a.length];
            for (int i = 0; i < a.length; ++i) {
                c[i] = a[i].intValue() + b[i].intValue();
            }
            return c;
        }
    },
    FLOAT(GL.GL_FLOAT, GLBuffers.SIZEOF_FLOAT)
    {

        public void put(ByteBuffer buf, int id, Number[] values)
        {
            for (int i = 0; i < values.length; i++) {
                buf.putFloat(id + i*byteSize, values[i].floatValue());
            }
        }

        public void get(ByteBuffer buf, int id, Number[] values)
        {
            for (int i = 0; i < values.length; i++) {
                values[i] = buf.getFloat(id + i*byteSize);
            }
        }

        public Number[] mul(double mul, Number[] a)
        {
            Float[] c = new Float[a.length];
            for (int i = 0; i < a.length; ++i) {
                c[i] = (float) (a[i].floatValue() * mul);
            }
            return c;
        }

        public Number[] add(Number[] a, Number[] b)
        {
            Float[] c = new Float[a.length];
            for (int i = 0; i < a.length; ++i) {
                c[i] =  a[i].floatValue() +  b[i].floatValue();
            }
            return c;
        }
    },
    DOUBLE(GL2GL3.GL_DOUBLE, GLBuffers.SIZEOF_DOUBLE)
    {

        public void put(ByteBuffer buf, int id, Number[] values)
        {
            for (int i = 0; i < values.length; i++) {
                buf.putDouble(id + i*byteSize, values[i].doubleValue());
            }
        }

        public void get(ByteBuffer buf, int id, Number[] values)
        {
            for (int i = 0; i < values.length; i++) {
                values[i] = buf.getDouble(id + i*byteSize);
            }
        }

        public Number[] mul(double mul, Number[] a)
        {
            Double[] c = new Double[a.length];
            for (int i = 0; i < a.length; ++i) {
                c[i] = a[i].doubleValue() * mul;
            }
            return c;
        }

        public Number[] add(Number[] a, Number[] b)
        {
            Double[] c = new Double[a.length];
            for (int i = 0; i < a.length; ++i) {
                c[i] = a[i].doubleValue() + b[i].doubleValue();
            }
            return c;
        }
    };

    public final int gl_const, byteSize;

    private DataType(int cst, int so)
    {
        gl_const = cst;
        byteSize = so;
    }

    /**
     * @return Gibt die OpenGL constante des Types zur�ck.
     */
    public int getGLConst()
    {
        return gl_const;
    }

    /**
     * @return Gibt die Gr��e des Types in der Anzahl der ben�tigten Bytes
     *         zur�ck.
     */
    public int getBSize()
    {
        return byteSize;
    }

    /**
     * Erstellt einen Bytbuffer.
     * <p/>
     * @param cap Legt die capazit�t des Bytebuffers abh�ngig der Byte Gr��e des
     *            Typs fest.
     * <p/>
     * @return Gibt den nue erstellten Bytebuffer zur�ck.
     */
    public ByteBuffer createBuffer(int cap)
    {
        return GLBuffers.newDirectByteBuffer(cap * getBSize());
    }

    /**
     * Legt ein Array von neuen Werten abh�ngig vom Typ im angegebenen Buffer
     * ab.
     * <p/>
     * @param buf Der ByteBuffer in den die neuen Werte abgelegt werden
     *               sollen.
     * @param values Werte die im Buffer abgelegt werden sollen.
     * <p/>
     * @throws AttributException wird geworfen falls die Werte nicht vom selben
     *                           Typ sind.
     */
    public abstract void put(ByteBuffer buf, int id, Number[] values);

    public abstract void get(ByteBuffer buf, int id, Number[] values);

    public abstract Number[] mul(double mul, Number[] a);

    public abstract Number[] add(Number[] a, Number[] b);
}
