/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.darwin.resourcehandling;

/**
 *
 * @author daniel
 */
public class WrongBuildArgsException extends RuntimeException {

    public WrongBuildArgsException(String message) {
        super(message);
    }

    public WrongBuildArgsException(String message, Throwable cause) {
        super(message, cause);
    }
}
