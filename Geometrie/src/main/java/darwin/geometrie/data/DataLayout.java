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
package darwin.geometrie.data;

import java.io.Serializable;
import java.util.*;

import com.google.common.base.Optional;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class DataLayout {

    private static final long serialVersionUID = 8468234920530037630L;

    public enum Format {

        /**
         * Erweitert die größe eines Vertex auf ein vielfaches von 2bit
         */
        AUTO,
        /**
         * Vertex Attribute werden einfach direkt hintereinander abgelegt.
         */
        INTERLEAVE,
        /**
         * Erweitert die größe eines Vertex auf ein vielfaches von 32bit
         */
        INTERLEAVE32;
    }
    private HashMap<Element, DataAttribut> alignments = new HashMap<>();
    private final int bytesize;
    private final Format format;

    public DataLayout(Element... elements) {
        this(Format.AUTO, elements);
    }

    public DataLayout(Format format, Element... elements) {
        if (elements.length == 1) {
            this.format = Format.INTERLEAVE;
        } else {
            this.format = format;
        }
        int[] offsets = new int[elements.length];
        for (int i = 0; i < elements.length - 1; ++i) {
            offsets[i + 1] = offsets[i] + elements[i].getVectorType().getByteSize();
        }

        int stride = offsets[offsets.length - 1]
                     + elements[elements.length - 1].getVectorType().getByteSize();

        if (elements.length > 1) {
            stride = calcStride(format, stride);
        }

        for (int i = 0; i < elements.length; ++i) {
            alignments.put(elements[i], new DataAttribut(stride, offsets[i]));
        }

        bytesize = stride;
    }

    public DataLayout(DataLayout layout, Element... elements) {
        this(layout.format, collect(layout.getElements(), elements));
    }

    private static Element[] collect(Collection<Element> ele,
                                     Element... elements) {
        Element[] re = new Element[ele.size() + elements.length];
        ele.toArray(re);
        System.arraycopy(elements, 0, re, ele.size(), elements.length);
        return re;
    }

    @SuppressWarnings("fallthrough")
    private int calcStride(Format f, int stride) {
        int mod = stride % 32;
        switch (f) {
            case INTERLEAVE:
                return stride;
            case AUTO:
                if (32 % stride == 0 || mod == 0) {
                    return stride;
                }
            case INTERLEAVE32:
                if (mod != 0) {
                    return stride + 32 - mod;
                } else {
                    return stride;
                }
        }

        assert false : "Unreachable statment, all possible case should have been handled";
        return 0;
    }

    public int getBytesize() {
        return bytesize;
    }

    public Collection<DataAttribut> getAttributs() {
        return alignments.values();
    }

    public DataAttribut getAttribut(Element a) {
        return alignments.get(a);
    }

    public Optional<Element> getElementByName(String name) {
        for (Element element : getElements()) {
            if (name.equals(element.getBezeichnung())) {
                return Optional.of(element);
            }
        }
        return Optional.absent();
    }

    public boolean hasElement(Element e) {
        return alignments.containsKey(e);
    }

    public Collection<Element> getElements() {
        return alignments.keySet();
    }

    public int getElementCount() {
        return getElements().size();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataLayout other = (DataLayout) obj;
//        alignments.keySet().equals(other.alignments.keySet());
        for (Element e : alignments.keySet()) {
            DataAttribut d1 = alignments.get(e);
            DataAttribut d2 = other.alignments.get(e);
            if (d1 != d2 && (d1 == null || !d1.equals(d2))) {
                return false;
            }
        }

        if (this.bytesize != other.bytesize) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.alignments);
        hash = 19 * hash + this.bytesize;
        hash = 19 * hash + (this.format != null ? this.format.hashCode() : 0);
        return hash;
    }
}
