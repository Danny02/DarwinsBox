/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.resourcehandling.resmanagment;

import darwin.resourcehandling.io.obj.ObjModelReader.Scale;
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
    @Deprecated
    private final boolean centered;
    @Deprecated
    private final Scale scale_type;
    @Deprecated
    private final float scale;
    private final ShaderDescription shader;

    public ObjConfig(String path, ShaderDescription descr) {
        this(path, false, Scale.ABSOLUTE, 1f, descr);
    }

    @Deprecated
    public ObjConfig(String path, boolean centered, Scale s2, float scale,
                     String descr) {
        this(path, centered, s2, scale, new ShaderDescription(descr));
    }

    @Deprecated
    public ObjConfig(String path, boolean centered, Scale s2, float scale,
                     ShaderDescription descr) {
        this.path = path;
        this.centered = centered;
        scale_type = s2;
        this.scale = scale;
        shader = descr;
    }

    @Deprecated
    public boolean isCentered() {
        return centered;
    }

    @Deprecated
    public Scale getScaleType() {
        return scale_type;
    }

    @Deprecated
    public float getScale() {
        return scale;
    }

    public String getPath() {
        return path;
    }

    public ShaderDescription getShader() {
        return shader;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ObjConfig other = (ObjConfig) obj;
        if ((this.path == null) ? (other.path != null) : !this.path.equals(
                other.path))
            return false;
        if (this.centered != other.centered)
            return false;
        if (this.scale_type != other.scale_type && (this.scale_type == null || !this.scale_type.
                equals(other.scale_type)))
            return false;
        if (this.scale != other.scale)
            return false;
        if (this.shader != other.shader && (this.shader == null || !this.shader.
                equals(other.shader)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.path != null ? this.path.hashCode() : 0);
        hash = 31 * hash + (this.centered ? 1 : 0);
        hash =
        31 * hash + (this.scale_type != null ? this.scale_type.hashCode() : 0);
        hash = 31 * hash + Float.floatToIntBits(this.scale);
        hash = 31 * hash + (this.shader != null ? this.shader.hashCode() : 0);
        return hash;
    }
}
