/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under the terms of the GNU General Public License as published by  *  the Free Software Foundation, either version 3 of the License, or  *  (at your option) any later version.  *   *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.core.gui;

import java.awt.*;

import darwin.renderer.util.memory.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public final class InfoPanel extends JPanel implements PerformanceView
{

    private static final long serialVersionUID = -7436523857585490394L;
    private float alpha;
    private JLabel fps = new JLabel("0,00");
    private JLabel perc = new JLabel("0,00");
    private JLabel free = new JLabel("0,00");
    private JLabel not = new JLabel("0,00");
    private JLabel total = new JLabel("0,00");

    public InfoPanel(float alpha)
    {
        setAlpha(alpha);
        iniComponents();
        setOpaque(false);
    }

    public void setAlpha(float alpha)
    {
        this.alpha = alpha;
    }

    @Override
    public void setFPS(double fps)
    {
        this.fps.setText(String.format("%.2f", fps));
    }

    @Override
    public void setMemInfo(MemoryInfo meminfo)
    {
        if (meminfo == null) {
            return;
        }
        double f = meminfo.getCurrentMemory() * 0.0009765625; // =: / 1024.;
        free.setText(String.format("%.2f", f));

        int t = meminfo.getTotalMemory();
        if (t == 0) {
            not.setText("???");
            total.setText("???");
            perc.setText("???");
        } else {
            double tt = t * 0.0009765625; // =: / 1024.;
            not.setText(String.format("%.2f", tt - f));
            total.setText(String.format("%.2f", tt));
            perc.setText(String.format("%.2f", meminfo.getFreeRatio() * 100.));
        }
    }

    private void iniComponents()
    {
        setMinimumSize(new Dimension(210, 135));
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        gc.anchor = GridBagConstraints.WEST;
        gc.ipadx = 5;
        add(new JLabel("FPS:"), gc);
        gc.gridy = 1;
        gc.insets = new Insets(7, 0, 0, 0);
        add(new JLabel("GPU Memory"), gc);
        gc.insets = new Insets(0, 2, 0, 0);
        gc.gridy = 2;
        add(new JLabel("Free (%):"), gc);
        gc.gridy = 3;
        add(new JLabel("Free (MiB):"), gc);
        gc.gridy = 4;
        add(new JLabel("Used (MiB):"), gc);
        gc.gridy = 5;
        add(new JLabel("Total (MiB):"), gc);

        gc.insets = new Insets(0, 0, 0, 2);
        gc.gridx = 1;
        gc.anchor = GridBagConstraints.EAST;
        gc.gridy = 0;
        add(fps, gc);
        gc.gridy = 2;
        add(perc, gc);
        gc.gridy = 3;
        add(free, gc);
        gc.gridy = 4;
        add(not, gc);
        gc.gridy = 5;
        add(total, gc);

        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(5);
        slider.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent e)
            {
                int val = ((JSlider) e.getSource()).getValue();
                setAlpha(val / 100f);
            }
        });
        slider.setOpaque(false);
        slider.setValue((int) (100 * alpha));

        gc.insets = new Insets(0, 2, 0, 2);
//        gc.ipadx = 0;
        gc.anchor = GridBagConstraints.CENTER;
        gc.gridx = 0;
        gc.gridy = 6;
        gc.gridwidth = 2;
        add(slider, gc);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        // make the checkbox semi-translucent
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
        Composite oldcomp = g2d.getComposite();
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 10, 10);
        g2d.setColor(Color.BLUE);
        Stroke s = new BasicStroke(5);
        g2d.setStroke(s);
        g2d.drawRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 10, 10);
        super.paintComponent(g2d);
        g2d.setComposite(oldcomp);
    }
}
