/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.dheinrich.darwin.renderer.geometrie.data;

import java.io.Serializable;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class DataAttribut implements Serializable{
    public final int stride, offset;
//    private final Element element;

    public DataAttribut(int stride, int offset) {
        this.stride = stride;
        this.offset = offset;
//        this.element = element;
    }

}
