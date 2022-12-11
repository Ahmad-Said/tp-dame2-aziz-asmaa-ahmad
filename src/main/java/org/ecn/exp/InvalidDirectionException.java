package org.ecn.exp;

/**
 * Exception elevated when position of pawn isn't in diagonal direction in the board
 */
public class InvalidDirectionException extends Exception{
    public InvalidDirectionException(String message) {
        super(message);
    }
}
