/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.core.timing;

/**
 *
 * @author daniel
 */
public interface StepListener {
    public void update(int tickCount, float lerp);
}
