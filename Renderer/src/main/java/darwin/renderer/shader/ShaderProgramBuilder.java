/*
 * Copyright (C) 2012 Daniel Heinrich <dannynullzwo@gmail.com>
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
package darwin.renderer.shader;

import java.util.Collection;
import java.util.LinkedList;

import darwin.renderer.GraphicContext;
import darwin.renderer.opengl.ShaderObjekt;
import darwin.renderer.opengl.ShaderProgramm;
import darwin.renderer.shader.BuildException.BuildError;

import com.google.common.base.Optional;
import javax.media.opengl.*;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class ShaderProgramBuilder {

    private final Collection<ShaderObjekt> objects = new LinkedList<>();
    private final Collection<ShaderAttribute> attributs = new LinkedList<>();

    public ShaderProgramBuilder with(ShaderObjekt object) {
        if (object != null) {
            objects.add(object);
        }
        return this;
    }

    public ShaderProgramBuilder with(Collection<ShaderAttribute> attribute) {
        if (attribute != null) {
            attributs.addAll(attribute);
        }
        return this;
    }

    public ShaderProgramm link(GraphicContext gc) throws BuildException {
        GL2GL3 gl = gc.getGL().getGL2GL3();
        int programObject = gl.glCreateProgram();

        for (ShaderObjekt so : objects) {
            gl.glAttachShader(programObject, so.getShaderobjekt());
        }

        //TODO GL Treiber Constanten in einen Globalen Constanten-Pool verfrachten
        int[] max = new int[1];
        gl.glGetIntegerv(GL2GL3.GL_MAX_VERTEX_ATTRIBS, max, 0);

        //TODO buggy, anscheined werden manche attribute ned gefunden
        for (ShaderAttribute sa : attributs) {
            int index = sa.getIndex();
            if (index >= 0 && index < max[0]) {
                gl.glBindAttribLocation(programObject, sa.getIndex(), sa.getName());
            }
        }
        
        gl.glProgramParameteri(programObject, GL2GL3.GL_PROGRAM_BINARY_RETRIEVABLE_HINT, GL.GL_TRUE);

        gl.glLinkProgram(programObject);

        ShaderProgramm prog = new ShaderProgramm(gc, programObject);
        Optional<String> error = prog.verify();
        if (error.isPresent()) {
            throw new BuildException(error.get(), BuildError.LinkTime);
        }

        return prog;
    }
}
