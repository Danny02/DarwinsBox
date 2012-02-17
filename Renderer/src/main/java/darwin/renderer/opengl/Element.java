/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer.opengl;

import java.io.Serializable;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class Element implements Serializable
{
    private static final long serialVersionUID = -8104245050243213668L;
    public final GLSLType gltype;
    public final String bezeichnung;

    public Element(GLSLType type, String bezeichnung) {
        this.gltype = type;
        this.bezeichnung = bezeichnung;
    }

    @Override
    public String toString() {
        return bezeichnung + " - " + gltype.name();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Element other = (Element) obj;
        if (this.gltype != other.gltype && (this.gltype == null || !this.gltype.equals(
                other.gltype)))
            return false;
        if ((this.bezeichnung == null) ? (other.bezeichnung != null) : !this.bezeichnung.
                equals(other.bezeichnung))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.gltype != null ? this.gltype.hashCode() : 0);
        hash =
        97 * hash + (this.bezeichnung != null ? this.bezeichnung.hashCode() : 0);
        return hash;
    }
}
