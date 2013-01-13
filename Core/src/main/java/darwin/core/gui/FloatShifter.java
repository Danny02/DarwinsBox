/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under the terms of the GNU General Public License as published by  *  the Free Software Foundation, either version 3 of the License, or  *  (at your option) any later version.  *   *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.core.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Ein Slider Component das es erlaubt wert �nderunge abzufragen. Der Slider
 * springt nach jeder Benutzung in seinen Ursprung zur�ck
 * <p/>
 * @author Daniel Heinrich
 */
public class FloatShifter extends JSlider
{

    private List<FloatValueListener> floatlistener = new ArrayList<>();
    private float grid;
    private int last = 0;

    public FloatShifter(float gridsize)
    {
        super(JSlider.HORIZONTAL, -100, 100, 0);
        grid = gridsize;
        setMajorTickSpacing(10);
        setMinorTickSpacing(200);
        setPaintTicks(true);

        addMouseListener(new MouseAdapter()
        {

            @Override
            public void mouseReleased(MouseEvent e)
            {
                last = 0;
                ((JSlider) e.getSource()).setValue(0);
            }
        });

        addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent e)
            {
                int val = ((JSlider) e.getSource()).getValue();
                fireChangeEvent(val - last);
                last = val;
            }
        });
    }

    private void fireChangeEvent(float f)
    {
        float val = f * 0.01f * grid;
        for (FloatValueListener flv : floatlistener) {
            flv.valueChanged(val);
        }
    }

    public void addFloatListener(FloatValueListener fvl)
    {
        floatlistener.add(fvl);
    }

    public void removeFloatListener(FloatValueListener fvl)
    {
        floatlistener.remove(fvl);
    }

    public void setGrid(float f)
    {
        grid = f;
    }
}
