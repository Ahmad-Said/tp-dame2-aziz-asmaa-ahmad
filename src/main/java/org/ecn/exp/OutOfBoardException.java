package org.ecn.exp;

/**
 * Exception elevated when new position is out of the board
 */
public class OutOfBoardException extends Exception{
    public OutOfBoardException(String message) {
        super(message);
    }
}
