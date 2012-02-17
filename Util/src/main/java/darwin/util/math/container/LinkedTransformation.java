/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package darwin.util.math.container;

import darwin.util.math.composits.*;

/**
 *
 * @author dheinrich
 */
public class LinkedTransformation extends SimpleTransformation{
    private TransformationContainer trans;

    public LinkedTransformation(TransformationContainer trans) {
        this.trans = trans;
    }

    @Override
    public ModelMatrix getModelMatrix() {
        ModelMatrix m = trans.getModelMatrix();
        return (ModelMatrix) m.mult(getMatrix(), m);
    }

}
