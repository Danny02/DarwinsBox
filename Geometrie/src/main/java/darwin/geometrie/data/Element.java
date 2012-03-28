/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.geometrie.data;

import java.io.Serializable;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class Element implements Serializable
{

    private static final long serialVersionUID = -8104245050243213668L;
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
        if (getClass() != obj.getClass()) {
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
