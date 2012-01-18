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
package de.dheinrich.darwin.ressourcen;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import de.dheinrich.darwin.resourcehandling.Resource;
import de.dheinrich.darwin.resourcehandling.ResourceBuilder;
import de.dheinrich.darwin.resourcehandling.ResourceState;
import de.dheinrich.darwin.resourcehandling.WrongBuildArgsException;
import de.dheinrich.darwin.resourcehandling.core.BaseResourceState;
import de.dheinrich.darwin.resourcehandling.test.StreamHandle;
import java.io.IOException;
import java.io.InputStream;
import javax.media.opengl.GLException;

/**
 *
 * @author daniel
 */
public class OpenGLTextureBuilder implements ResourceBuilder<OpenGLTexture> {

    @Override
    public ResourceState neededBaseState() {
        return new BaseResourceState(null);
    }

    @Override
    public OpenGLTexture createResource(Resource base) throws WrongBuildArgsException {
        if (!(base instanceof StreamHandle)) {
            throw new WrongBuildArgsException("Das übergebene Handle ist kein Stream");
        }

        StreamHandle handle = (StreamHandle) base;
        Texture newtex = null;
        try (InputStream stream = handle.getStream()) {
            newtex = TextureIO.newTexture(stream, true, null);
        } catch (IOException | GLException ex) {
            throw new WrongBuildArgsException("Beim laden der Textur ist ein unerwarteter Fehler aufgetreten", ex);
        }
        return new OpenGLTexture(newtex, handle);
    }
}
