package org.ecn;

import com.sun.xml.internal.bind.InternalAccessorFactory;

import java.sql.SQLOutput;

public class BoardController {
    Board board;

    public BoardController(Board board) {
        this.board = board;
    }

    public void pickTargetPion(boolean isWhitePlayer) {
        System.out.println(board);
        System.out.println("Pick " + (isWhitePlayer ? "White [1 or 2]" : "Black [2 or 3]") + " location to move:");
        Integer rowIndex;
        Integer colIndex;
        do {
            rowIndex = InterfaceUtils.getIndexChoice(board.getTailleBoard() + 1, "Chosen Row");
            colIndex = InterfaceUtils.getIndexChoice(board.getTailleBoard() + 1, "Chosen Column");
        } while (!board.isWhitePlayer(rowIndex, colIndex));

    }

    public void moveItem(Integer rowIndex, Integer colIndex) {
        System.out.println("Choose item row to move:");
        System.out.println("Choose a diagonal direction from 1-3-7 or 9 as follow : ");
        System.out.println("7\t-\t9");
        System.out.println("-\tJ\t-");
        System.out.println("1\t-\t3");
        Integer moveBehaviorDirection = InterfaceUtils.getIndexChoice(10, "Chosen Direction");
        board.moveItem(rowIndex, colIndex, moveBehaviorDirection);
    }
}
