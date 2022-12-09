package org.ecn;

import lombok.Data;

import java.util.Arrays;

/**
 * Case vide => 0
 * Case occupe by black  => 1
 * dame black => 2
 * Case occupe by white => 3
 * dame white => 4
 */
@Data
public class Board {
    private int[][] board;

    private int tailleBoard;

    public Board(int tailleBoard) {
        this.tailleBoard = tailleBoard;
        initializeBoard();
    }

    private void initializeBoard() {
        board = new int[tailleBoard + 1][tailleBoard + 1];
        for (int i = 1; i < board.length; i++) {
            for (int j = 1; j < board[i].length; j++) {
                if (i <= tailleBoard / 2 - 1 && i % 2 != j % 2) {
                    board[i][j] = 1;
                }
                if (i >= tailleBoard / 2 + 2 && i % 2 != j % 2) {
                    board[i][j] = 3;
                }
            }
        }
    }

    public String getLegend() {
        StringBuilder st = new StringBuilder();
        st.append("* Case vide => 0").append("\n");
        st.append("* Case occupe by black  => 1").append("\n");
        st.append("* dame black => 2").append("\n");
        st.append("* Case occupe by white => 3").append("\n");
        st.append("* dame white => 4").append("\n");
        return st.toString();
    }

    @Override
    public String toString() {
        StringBuilder st = new StringBuilder(board.length * board[0].length);
        // append first line index
        st.append(getLegend());
        st.append("Board: \n");
        st.append("r\\c ");
        for (int i = 1; i < board.length; i++) {
            st.append(i % 10).append(" ");
        }
        st.append("\n");
        for (int i = 1; i < board.length; i++) {
            st.append(i % 10 + "   ");
            for (int j = 1; j < board[i].length; j++) {
                st.append(board[i][j]).append(j == board[i].length - 1 ? "" : " ");
            }
            st.append("\n");
        }
        return st.toString();
    }

    public void moveItem(int rowIndex, int colIndex, Integer moveBehaviorDirection) {
    }

    public boolean isWhitePlayer(Integer rowIndex, Integer colIndex) {
        return false;
    }
}
