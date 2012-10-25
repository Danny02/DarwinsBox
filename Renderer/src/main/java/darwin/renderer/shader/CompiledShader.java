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

import java.nio.*;
import java.util.Arrays;

import darwin.renderer.GraphicContext;
import darwin.renderer.opengl.ShaderProgramm;

import com.google.common.base.Optional;
import com.jogamp.common.nio.Buffers;
import javax.media.opengl.*;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class CompiledShader {

    public static final String EXTENSION_STRING = "GL_ARB_get_program_binary";
    
    private static int[] formats;
    
    private final int format;
    private final Buffer data;

    public CompiledShader(int format, Buffer data) {
        this.format = format;
        this.data = data;
    }
    
    public static CompiledShader fromShader(GraphicContext gc, ShaderProgramm prog)
    {
        GLES2 gl = gc.getGL().getGLES2();
        
        int pid = prog.getPObject();
        IntBuffer length = Buffers.newDirectIntBuffer(1);
        gl.glGetProgramiv(pid, GL2.GL_PROGRAM_BINARY_LENGTH, length);
        
        IntBuffer format = Buffers.newDirectIntBuffer(1);
        ByteBuffer data = Buffers.newDirectByteBuffer(length.get(0));
        gl.glGetProgramBinary(pid, length.get(0), length, format, data);
        
        if(gl.glGetError() == GL.GL_INVALID_OPERATION)
        {
            //invalid state can occure when program isn't linked or the buffer is to smal
            throw new RuntimeException("The given shader program is not in a valid state(not linked correctly)");
        }
        
        return new CompiledShader(format.get(0), data);
    }

    public Optional<ShaderProgramm> toShader(GraphicContext gc) {        
        GL2ES2 gl = gc.getGL().getGL2();

        int programObject = gl.glCreateProgram();
        ShaderProgramm prog = new ShaderProgramm(gc, programObject);
        
        gl.glProgramBinary(prog.getPObject(), format, data, data.limit());
        if(GLES2.GL_INVALID_ENUM == gl.glGetError())
        {
            return Optional.absent();
        }

        Optional<String> error = prog.verify();
        if (error.isPresent()) {
            return Optional.of(prog);
        } else {
            return Optional.absent();
        }
    }
    
    public boolean isFormatSupported(GraphicContext gc)
    {
        if(formats == null)
        {
            initializeFormats(gc);
        }
        
        return Arrays.binarySearch(formats, format) >= 0;
    }
    
    public synchronized static void initializeFormats(GraphicContext gc)
    {
        GL gl = gc.getGL();
        
        IntBuffer c = Buffers.newDirectIntBuffer(1);        
        gl.glGetIntegerv(GL2ES2.GL_NUM_PROGRAM_BINARY_FORMATS, c);
        
        c = Buffers.newDirectIntBuffer(c.get(0));
        gl.glGetIntegerv(GL2ES2.GL_PROGRAM_BINARY_FORMATS, c);
        
        formats = c.array();
    }
    
    public static boolean isAvailable(GraphicContext gc)
    {
        return gc.getGL().isExtensionAvailable(EXTENSION_STRING);
    }

    public Buffer getData() {
        return data;
    }

    public int getFormat() {
        return format;
    }
}
