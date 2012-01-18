/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.renderer.geometrie.data;

import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2GL3;

import com.jogamp.opengl.util.GLBuffers;

/**
 * Definiert DatenTypen die in einem Vertex Attribut Array genutzt werden k�nnen.
 * @author Daniel Heinrich
 */
public enum DataType {

    SHORT(GL.GL_SHORT, GLBuffers.SIZEOF_SHORT) {

        public void put(ByteBuffer buf, int id, Number[] values) {
            for (int i = 0; i < values.length; i++) {
                buf.putShort(id + 1, (Short) values[i]);
            }
        }

        public void get(ByteBuffer buf, int id, Number[] values) {
            for (int i = 0; i < values.length; i++) {
                values[i] = buf.getShort(id + i);
            }
        }

        public Number[] mul(double mul, Number[] a) {
            Short[] c = new Short[a.length];
            for (int i = 0; i < a.length; ++i) {
                c[i] = (short) ((Short) a[i] * mul);
            }
            return c;
        }

        public Number[] add(Number[] a, Number[] b) {
            Short[] c = new Short[a.length];
            for (int i = 0; i < a.length; ++i) {
                c[i] = (short) ((Short) a[i] + (Short) b[i]);
            }
            return c;
        }
    },
    INT(GL2GL3.GL_INT, GLBuffers.SIZEOF_INT) {

        public void put(ByteBuffer buf, int id, Number[] values) {
            for (int i = 0; i < values.length; i++) {
                buf.putInt(id + i, (Integer) values[i]);
            }
        }

        public void get(ByteBuffer buf, int id, Number[] values) {
            for (int i = 0; i < values.length; i++) {
                values[i] = buf.getInt(id + i);
            }
        }

        public Number[] mul(double mul, Number[] a) {
            Integer[] c = new Integer[a.length];
            for (int i = 0; i < a.length; ++i) {
                c[i] = (int) ((Integer) a[i] * mul);
            }
            return c;
        }

        public Number[] add(Number[] a, Number[] b) {
            Integer[] c = new Integer[a.length];
            for (int i = 0; i < a.length; ++i) {
                c[i] = (Integer) a[i] + (Integer) b[i];
            }
            return c;
        }
    },
    FLOAT(GL.GL_FLOAT, GLBuffers.SIZEOF_FLOAT) {

        public void put(ByteBuffer buf, int id, Number[] values) {
            for (int i = 0; i < values.length; i++) {
                buf.putFloat(id + i, (float)(Float) values[i]);
            }
        }

        public void get(ByteBuffer buf, int id, Number[] values) {
            for (int i = 0; i < values.length; i++) {
                values[i] = buf.getFloat(id + i);
            }
        }

        public Number[] mul(double mul, Number[] a) {
            Float[] c = new Float[a.length];
            for (int i = 0; i < a.length; ++i) {
                c[i] = (float) ((Float) a[i] * mul);
            }
            return c;
        }

        public Number[] add(Number[] a, Number[] b) {
            Float[] c = new Float[a.length];
            for (int i = 0; i < a.length; ++i) {
                c[i] = (Float) a[i] + (Float) b[i];
            }
            return c;
        }
    },
    DOUBLE(GL2GL3.GL_DOUBLE, GLBuffers.SIZEOF_DOUBLE) {

        public void put(ByteBuffer buf, int id, Number[] values) {
            for (int i = 0; i < values.length; i++) {
                buf.putDouble(id + i, (Double) values[i]);
            }
        }

        public void get(ByteBuffer buf, int id, Number[] values) {
            for (int i = 0; i < values.length; i++) {
                values[i] = buf.getDouble(id + i);
            }
        }

        public Number[] mul(double mul, Number[] a) {
            Double[] c = new Double[a.length];
            for (int i = 0; i < a.length; ++i) {
                c[i] = (Double) a[i] * mul;
            }
            return c;
        }

        public Number[] add(Number[] a, Number[] b) {
            Double[] c = new Double[a.length];
            for (int i = 0; i < a.length; ++i) {
                c[i] = (Double) a[i] + (Double) b[i];
            }
            return c;
        }
    },;
    private int gl_const, size_of;

    private DataType(int cst, int so) {
        gl_const = cst;
        size_of = so;
    }

    /**
     * @return
     * Gibt die OpenGL constante des Types zur�ck.
     */
    public int getGLConst() {
        return gl_const;
    }

    /**
     * @return
     * Gibt die Gr��e des Types in der Anzahl der ben�tigten Bytes zur�ck.
     */
    public int getBSize() {
        return size_of;
    }

    /**
     * Erstellt einen Bytbuffer.
     * @param cap
     * Legt die capazit�t des Bytebuffers abh�ngig der Byte Gr��e des Typs fest.
     * @return
     * Gibt den nue erstellten Bytebuffer zur�ck.
     */
    public ByteBuffer createBuffer(int cap) {
        return GLBuffers.newDirectByteBuffer(cap * getBSize());
    }

    /**
     * Legt ein Array von neuen Werten abh�ngig vom Typ im angegebenen Buffer ab.
     * @param buf
     * Der ByteBuffer in den die neuen Werte abgelegt werden sollen.
     * @param values
     * Werte die im Buffer abgelegt werden sollen.
     * @throws AttributException
     * wird geworfen falls die Werte nicht vom selben Typ sind.
     */
    public abstract void put(ByteBuffer buf, int id, Number[] values);

    public abstract void get(ByteBuffer buf, int id, Number[] values);

    public abstract Number[] mul(double mul, Number[] a);

    public abstract Number[] add(Number[] a, Number[] b);

}
