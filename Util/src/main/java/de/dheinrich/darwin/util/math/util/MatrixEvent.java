/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.util.math.util;

import javax.swing.event.ChangeEvent;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class MatrixEvent extends ChangeEvent
{
    private final MatType type;

    public MatrixEvent(MatrixCache sm, MatType t) {
        super(sm);
        type = t;
    }

    public MatType getType() {
        return type;
    }

    @Override
    public MatrixCache getSource() {
        return (MatrixCache) super.getSource();
    }
}
