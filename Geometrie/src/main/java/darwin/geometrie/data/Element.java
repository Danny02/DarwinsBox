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

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class Element
{
    private final VectorType vectorType;
    private final String bezeichnung;

    public Element(VectorType type, String bezeichnung)
    {
        this.vectorType = type;
        this.bezeichnung = bezeichnung;
    }

    public DataType getDataType()
    {
        return vectorType.getDataType();
    }

    public VectorType getVectorType()
    {
        return vectorType;
    }

    public String getBezeichnung()
    {
        return bezeichnung;
    }

    @Override
    public String toString()
    {
        return bezeichnung + " - " + vectorType.getDataType().name()
                + vectorType.getElementCount();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Element)) {
            return false;
        }
        final Element other = (Element) obj;
        if (this.vectorType != other.vectorType && (this.vectorType == null || !this.vectorType.equals(
                other.vectorType))) {
            return false;
        }
        if ((this.bezeichnung == null) ? (other.bezeichnung != null) : !this.bezeichnung.equals(other.bezeichnung)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 97 * hash + (this.vectorType != null ? this.vectorType.hashCode() : 0);
        hash =
                97 * hash + (this.bezeichnung != null ? this.bezeichnung.hashCode() : 0);
        return hash;
    }
}
