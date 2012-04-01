/*
 * Copyright (C) 2011 daniel
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

/**
 *
 * @author daniel
 */
public class BuildException extends Exception
{

    private final BuildError type;
    private final String msg;

    public enum BuildError
    {

        CompileTime, LinkTime;
    }

    public BuildException(String message, BuildError errortype)
    {
        msg = message;
        type = errortype;
    }

    public BuildException(String message, Throwable cause, BuildError errortype)
    {
        super(cause);
        msg = message;
        type = errortype;
    }

    public BuildError getErrorType()
    {
        return type;
    }

    @Override
    public String getMessage()
    {
        return msg;
    }
}
