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
package darwin.resourcehandling.resmanagment;

import darwin.resourcehandling.handle.ResourceBundle;

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
    private final ResourceBundle shader;

    public ObjConfig(String path, ResourceBundle shader) {
        this.path = path;
        this.shader = shader;
    }


    public String getPath()
    {
        return path;
    }

    public ResourceBundle getShaderResource()
    {
        return shader;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if(obj == this)
        	return true;
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
