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
package darwin.renderer.util.memory;

/**
 *
 * @author daniel
 */
public class DummyMemInfo extends MemoryInfo
{

    @Override
    public int getTotalMemory()
    {
        return 0;
    }

    @Override
    public int getCurrentMemory()
    {
        return 0;
    }

    @Override
    public String getStatus()
    {
        return "No information can be collected about the graphic device memory!";
    }

    @Override
    public double getFreeRatio()
    {
        return 1;
    }

}
