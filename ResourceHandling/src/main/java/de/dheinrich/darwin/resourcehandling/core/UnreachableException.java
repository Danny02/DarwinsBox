/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.darwin.resourcehandling.core;

/**
 *
 * @author daniel
 */
public class UnreachableException extends RuntimeException {

    public UnreachableException(String message, Throwable cause) {
        super(message, cause);
    }
}
