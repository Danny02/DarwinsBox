/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.ressourcen.resmanagment.texture;

import com.jogamp.opengl.util.texture.*;
import de.dheinrich.darwin.ressourcen.io.*;
import java.io.*;
import javax.media.opengl.*;
import org.apache.log4j.*;

import static de.dheinrich.darwin.renderer.GraphicContext.*;
import static de.dheinrich.darwin.ressourcen.resmanagment.RessourcesLoader.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class HeightMapLoadJob extends TextureLoadJob
{
    private static class Log
    {
        private static Logger ger = Logger.getLogger(HeightMapLoadJob.class);
    }

    public HeightMapLoadJob(String path) {
        super(path, -1, -1);
    }

    @Override
    public Texture load() {
        InputStream is = getRessource(getPath());
        Texture re = null;
        try {
            if (is == null) {
                Log.ger.fatal("Ressource \"" + getPath()
                        + "\" konnte nicht gefunden werden.");
                throw new IOException("Error loading file " + getPath());
            }
            String[] suffix = getPath().split("\\.");
            TextureData td = TextureIO.newTextureData(getGL().getGLProfile(), is,
                                                 GL2GL3.GL_LUMINANCE_FLOAT32_ATI,
                                                 GL2GL3.GL_LUMINANCE, true,
                                                 suffix[suffix.length - 1]);


            re = TextureIO.newTexture(td);
            TextureUtil.setTexturePara(re, GL.GL_LINEAR, GL.GL_CLAMP_TO_EDGE);
            tcontainer.setTexture(re);
        } catch (IOException ex) {
            Log.ger.fatal("Heigthmap " + getPath()
                    + " konnte nicht geladen werden.\n("
                    + ex.getLocalizedMessage() + ")", ex);
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException ex) {
                }
        }

        return re;
    }
}
