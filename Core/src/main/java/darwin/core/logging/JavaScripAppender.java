/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.core.logging;

import org.apache.log4j.*;
import org.apache.log4j.spi.*;

/**
 *
 * @author daniel
 */
public class JavaScripAppender extends AppenderSkeleton {

    private Layout l = new SimpleLayout();

    @Override
    protected void append(LoggingEvent event) {
        String msg = l.format(event).trim();
        String style = "default";
        switch (event.getLevel().toInt()) {
            case Level.WARN_INT:
                style = "warning";
                break;
            case Level.ERROR_INT:
                style = "error";
                break;
            case Level.FATAL_INT:
                style = "fatal";
        }
        throw new UnsupportedOperationException("JavaScript nicht implementiert");
//        try {
//            JSObject window = JSObject.getWindow(applet);
//            window.eval("print('" + style + "', '" + msg + "');");
//        } catch (JSException ex) {
//            new Exception("Der Aufruf der 'print' javascript Function,"
//                    + " hat eine Exception geworfen!", ex).printStackTrace();
//        }
    }

    @Override
    public void close() {
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
