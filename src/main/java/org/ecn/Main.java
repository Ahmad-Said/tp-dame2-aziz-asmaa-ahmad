package org.ecn;

public class Main {
    public static void main(String[] args) {
        Board board = new Board(InputIntegerUtils.getBoundedInteger(4, 14, "Enter Board size"));
        BoardController boardController = new BoardController(board, "Player one", "Player two");

        boardController.startGame();
    }
}
