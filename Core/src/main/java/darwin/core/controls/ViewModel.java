/*
 *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.core.controls;

import darwin.util.math.composits.ViewMatrix;

/**
 *
 * @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public interface ViewModel
{
    /**
     *
     * @return always returns the same reference
     */
    public ViewMatrix getView();

    public void dragged(float dx, float dy);

    public void steps(int steps, boolean ctrl, boolean shift);

    public void resetView();

    public void resetViewX();

    public void resetViewY();

    public void resetViewZ();

    public void addListener(ViewListener listener);

    public void removeListener(ViewListener listener);
}
