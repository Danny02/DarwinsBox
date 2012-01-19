/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.darwin.core.logging;

import org.apache.log4j.*;

/**
 *
 * @author daniel
 */
public class Log4JLogger implements LoggerBase{
    private final Logger logger;

    public Log4JLogger(Logger logger) {
        this.logger = logger;
    }

    public void warn(Object message, Throwable t) {
        logger.warn(message, t);
    }

    public void warn(Object message) {
        logger.warn(message);
    }

    public void info(Object message, Throwable t) {
        logger.info(message, t);
    }

    public void info(Object message) {
        logger.info(message);
    }

    public void fatal(Object message, Throwable t) {
        logger.fatal(message, t);
    }

    public void fatal(Object message) {
        logger.fatal(message);
    }

    public void error(Object message, Throwable t) {
        logger.error(message, t);
    }

    public void error(Object message) {
        logger.error(message);
    }

    public void debug(Object message, Throwable t) {
        logger.debug(message, t);
    }

    public void debug(Object message) {
        logger.debug(message);
    }

    public void trace(Object message, Throwable t) {
        logger.trace(message, t);
    }

    public void trace(Object message) {
        logger.trace(message);
    }

}
