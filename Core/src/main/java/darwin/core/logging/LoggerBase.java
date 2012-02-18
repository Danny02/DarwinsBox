/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.core.logging;

/**
 *
 * @author daniel
 */
public interface LoggerBase {

    public void warn(Object message, Throwable t);

    public void warn(Object message);

    public void info(Object message, Throwable t);

    public void info(Object message);

    public void fatal(Object message, Throwable t);

    public void fatal(Object message);

    public void error(Object message, Throwable t);

    public void error(Object message);

    public void debug(Object message, Throwable t);

    public void debug(Object message);

    public void trace(Object message, Throwable t);

    public void trace(Object message);
}