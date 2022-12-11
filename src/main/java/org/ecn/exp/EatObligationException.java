package org.ecn.exp;

/**
 * Exception elevated when movement is discarding eat obligation rule.
 */
public class EatObligationException extends Exception{
    public EatObligationException(String message) {
        super(message);
    }
}
