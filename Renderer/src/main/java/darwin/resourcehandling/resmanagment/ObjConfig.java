/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.resourcehandling.resmanagment;

import darwin.resourcehandling.resmanagment.texture.ShaderDescription;

/**
 *
 * @author dheinrich
 */
//TODO es sollten keine scalierungen, zentrierung statfinden beim laden
//sondern anstatt, nachträglich mit einer start model matrix festgelegt werden
//solche veränderungen der modell daten gehören rein in den build prozess
public class ObjConfig
{

    private final String path;
    private final ShaderDescription shader;

    public ObjConfig(String path, String descr)
    {
        this(path, new ShaderDescription(descr));
    }

    public ObjConfig(String path, ShaderDescription descr)
    {
        this.path = path;
        shader = descr;
    }

    public String getPath()
    {
        return path;
    }

    public ShaderDescription getShader()
    {
        return shader;
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
        final ObjConfig other = (ObjConfig) obj;
        if ((this.path == null) ? (other.path != null) : !this.path.equals(
                other.path)) {
            return false;
        }
        if (this.shader != other.shader && (this.shader == null || !this.shader.equals(other.shader))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 31 * hash + (this.path != null ? this.path.hashCode() : 0);
        hash = 31 * hash + (this.shader != null ? this.shader.hashCode() : 0);
        return hash;
    }
}
