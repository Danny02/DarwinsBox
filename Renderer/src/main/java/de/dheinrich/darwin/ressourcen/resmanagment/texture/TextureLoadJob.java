/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.ressourcen.resmanagment.texture;

import com.jogamp.opengl.util.texture.*;
import de.dheinrich.darwin.ressourcen.io.*;
import de.dheinrich.darwin.ressourcen.resmanagment.*;
import de.dheinrich.darwin.ressourcen.wrapper.*;
import java.io.*;
import javax.media.opengl.*;
import org.apache.log4j.*;

import static de.dheinrich.darwin.renderer.GraphicContext.*;

/**
 *
 * @author dheinrich
 */
public class TextureLoadJob implements LoadJob<Texture>
{
    private static class Log
    {
        private static Logger ger = Logger.getLogger(TextureLoadJob.class);
    }
    private static final String texturepath = "resources/Textures/";
    private final String path;
    private final int filtering, wrapping;
    protected TextureContainer tcontainer;

    public TextureLoadJob(String path, int filtering, int wrapping) {
        this.path = texturepath + path;
        this.filtering = filtering;
        this.wrapping = wrapping;
//        testcopy();
    }

    public TextureLoadJob(File file, int filtering, int wrapping) {
        this.path = file.getAbsolutePath();
        this.filtering = filtering;
        this.wrapping = wrapping;
    }

    public int getFiltering() {
        return filtering;
    }

    public String getPath() {
        return path;
    }

    public int getWrapping() {
        return wrapping;
    }

    public void setCon(TextureContainer tcontainer) {
        this.tcontainer = tcontainer;
    }

    @Override
    public Texture load() {
        GL gl = getGL();
        Texture re = tcontainer.getTexture();
        if (re != null)
            re.destroy(gl);
        try {
            re = TextureUtil.loadTexture(path, filtering,
                                         wrapping);
            if (filtering == GL.GL_LINEAR) {
                re.setTexParameteri(gl, GL.GL_TEXTURE_MIN_FILTER,
                                    GL.GL_LINEAR_MIPMAP_LINEAR);
                re.setTexParameteri(gl, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            }
        } catch (IOException ex) {
            Log.ger.warn("Texture " + path
                    + " konnte nicht geladen werden.\n("
                    + ex.getLocalizedMessage() + ")");
            try {
                re = TextureUtil.loadTexture(texturepath + "error.dds",
                                             GL.GL_NEAREST,
                                             wrapping);
            } catch (IOException ex1) {
                Log.ger.error("Keine Error Texturen gefunden.");
            }
        }
        tcontainer.setTexture(re);
        return re;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TextureLoadJob other = (TextureLoadJob) obj;
        if ((this.path == null) ? (other.path != null) : !this.path.equals(
                other.path))
            return false;
        if (this.filtering != other.filtering)
            return false;
        if (this.wrapping != other.wrapping)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.path != null ? this.path.hashCode() : 0);
        hash = 53 * hash + this.filtering;
        hash = 53 * hash + this.wrapping;
        return hash;
    }
}
