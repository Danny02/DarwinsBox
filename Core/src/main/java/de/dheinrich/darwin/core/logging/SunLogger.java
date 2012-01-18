/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.darwin.core.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniel
 */
public class SunLogger implements LoggerBase {

    private final Logger logger;

    public SunLogger(Logger logger) {
        this.logger = logger;
    }

    public void warn(Object message, Throwable t) {
        logger.log(Level.WARNING, message.toString(), t);
    }

    public void warn(Object message) {
        logger.log(Level.WARNING, message.toString());
    }

    public void info(Object message, Throwable t) {
        logger.log(Level.FINER, message.toString(), t);
    }

    public void info(Object message) {
        logger.log(Level.FINER, message.toString());
    }

    public void fatal(Object message, Throwable t) {
        logger.log(Level.SEVERE, message.toString(), t);
    }

    public void fatal(Object message) {
        logger.log(Level.SEVERE, message.toString());
    }

    public void error(Object message, Throwable t) {
        logger.log(Level.SEVERE, message.toString(), t);
    }

    public void error(Object message) {
        logger.log(Level.SEVERE, message.toString());
    }

    public void debug(Object message, Throwable t) {
        logger.log(Level.FINE, message.toString(), t);
    }

    public void debug(Object message) {
        logger.log(Level.FINE, message.toString());
    }

    public void trace(Object message, Throwable t) {
        logger.log(Level.FINEST, message.toString(), t);
    }

    public void trace(Object message) {
        logger.log(Level.FINEST, message.toString());
    }
}
