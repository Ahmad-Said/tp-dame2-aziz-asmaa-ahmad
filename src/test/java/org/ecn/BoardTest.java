package org.ecn;

import org.ecn.exp.EatObligationException;
import org.ecn.exp.InvalidDirectionException;
import org.ecn.exp.OutOfBoardException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    private Board board;
    int blackQueenRow;
    int blackQueenCol;
    int whitePawnRow;
    int whitePawnCol;

    @BeforeEach
    void setUp() {
        this.board = new Board(10);
        System.out.println(Board.prettyPrintLegend());
        System.out.println("Allowed directions as numpad:\n" +
                "7\t-\t9" + "\n" +
                "-\tP\t-" + "\n" +
                "1\t-\t3" + "\n");
        board.clearBoard();
        blackQueenRow = 5;
        blackQueenCol = 1;
        whitePawnRow = 5;
        whitePawnCol = 5;
        board.getBoardArray()[whitePawnRow][whitePawnCol] = Board.WHITE_PAWN;
        board.getBoardArray()[blackQueenRow][blackQueenCol] = Board.BLACK_QUEEN;
    }

    @AfterEach
    void tearDown() {
        if (board != null) {
            System.out.println("Test end.");
            System.out.println(board);
            System.out.println(board.prettyPrintWinningColor());
        }
    }

    @ParameterizedTest
    // move down, right, up, left and stay at place
    @ValueSource(ints = {2, 6, 8, 4, 5})
    void moveInInvalidDirection(int direction) {
        Exception e;
        System.out.println("Direction " + direction + " from Black Queen at " + blackQueenRow + ", " + blackQueenCol);
        e = assertThrows(InvalidDirectionException.class, () -> board.moveItem(blackQueenRow, blackQueenCol, direction, 1));
        System.err.println(e.getMessage());

        System.out.println();
        System.out.println("Direction " + direction + " from White Pawn at " + whitePawnRow + ", " + whitePawnCol);
        e = assertThrows(InvalidDirectionException.class, () -> board.moveItem(whitePawnRow, whitePawnCol, direction, 1));
        System.err.println(e.getMessage());
    }

    @Test
    void pawnFreeMoveTest() {
        board.clearBoard();
        // adding white pawn at top right
        whitePawnRow = 2;
        whitePawnCol = 10;
        board.getBoardArray()[whitePawnRow][whitePawnCol] = Board.WHITE_PAWN;
        System.out.println(board);
        Exception exception;
        try {
            // test moving in non-diagonal direction
            System.out.println("Direction 4 from 2, 10");
            exception = assertThrows(InvalidDirectionException.class, () -> board.moveItem(whitePawnRow, whitePawnCol, 4, 1));
            System.err.println("!! Exception 1. " + exception.getMessage());

            // test moving out of the board
            System.out.println();
            System.out.println("Direction 9 from 2, 10");
            exception = assertThrows(OutOfBoardException.class, () -> board.moveItem(whitePawnRow, whitePawnCol, 9, 1));
            System.err.println("!! Exception 2. " + exception.getMessage());

            System.out.println();
            System.out.println("Direction 7 from 2, 10");
            // True -> turn end as no eat obligation is present
            assertTrue(board.moveItem(whitePawnRow, whitePawnCol, 7, 1));
            assertEquals(Board.EMPTY_PLACE, board.getBoardArray()[whitePawnRow][whitePawnCol]); // old position is cleared
            assertEquals(Board.WHITE_QUEEN, board.getBoardArray()[1][9]); // new position is 1, 9 as white queen
        } catch (InvalidDirectionException | OutOfBoardException | EatObligationException e) {
            e.printStackTrace();
        }
    }

    @Test
    void pawnAttackTest() {
        Exception exception;
        try {
            // Eat obligation rules test
            // add black pawn at position 4, 4
            board.getBoardArray()[4][4] = Board.BLACK_PAWN;
            System.out.println(board);

            // test to eat added black pawn from 5, 5 location
            // ensure direction lead to eat obligation
            System.out.println("Direction 9 from 5, 5");
            exception = assertThrows(EatObligationException.class, () -> board.moveItem(whitePawnRow, whitePawnCol, 9, 1));
            System.err.println("!! Exception 1. " + exception.getMessage());

            // add black pawn at position 3, 3
            System.out.println();
            System.out.println("Adding black pawn at 3,3");
            board.getBoardArray()[3][3] = Board.BLACK_PAWN;
            System.out.println(board);
            // Trying to escape ensuring no more obligation is present as space is blocked by added pawn
            System.out.println("Direction 3 from 5, 5");
            assertTrue(board.moveItem(whitePawnRow, whitePawnCol, 3, 1));
            System.out.println(board);
            // get back the white pawn to its previous location
            System.out.println("Rollback move to 5, 5");
            assertTrue(board.moveItem(6, 6, 7, 1));
            System.out.println(board);

            // create enchained attack by making space between 4,4 and 3,3
            System.out.println("Creating space between black pawns to enable enchained attack");
            assertTrue(board.moveItem(3, 3, 7, 1));
            System.out.println(board);
            // eating first pawn at position 4, 4
            System.out.println("Direction 7 from 5, 5");
            assertFalse(board.moveItem(whitePawnRow, whitePawnCol, 7, 1));
            assertEquals(Board.EMPTY_PLACE, board.getBoardArray()[5][5]);
            assertEquals(Board.WHITE_PAWN, board.getBoardArray()[3][3]);
            assertEquals(Board.DEAD_PAWN, board.getBoardArray()[4][4]);
            whitePawnRow = whitePawnCol = 3;
            System.out.println(board);

            // eating second pawn at position 2, 2
            // check eat obligation
            System.out.println("Direction 1 from 3, 3");
            exception = assertThrows(EatObligationException.class, () -> board.moveItem(whitePawnRow, whitePawnCol, 1, 1));
            System.err.println("!! Exception 2. " + exception.getMessage());

            System.out.println();
            System.out.println("Direction 7 from 3, 3");
            assertTrue(board.moveItem(whitePawnRow, whitePawnCol, 7, 1));
            assertEquals(Board.EMPTY_PLACE, board.getBoardArray()[whitePawnRow][whitePawnCol]); // old white pawn location is empty
            assertEquals(Board.WHITE_QUEEN, board.getBoardArray()[1][1]); // new white pawn location as well it is promoted to queen on 1, 1
            assertEquals(Board.EMPTY_PLACE, board.getBoardArray()[4][2]); // dead pawn is cleared
            assertEquals(Board.EMPTY_PLACE, board.getBoardArray()[3][3]); // white pawn is empty
            whitePawnRow = whitePawnCol = 1;
        } catch (InvalidDirectionException | OutOfBoardException | EatObligationException e) {
            e.printStackTrace();
        }
    }

    @Test
    void queenMoveTest() {
        System.out.println(board);
        Exception exception;
        try {
            // test moving in non-diagonal direction
            System.out.println("Direction 4, steps 1 from 5, 1");
            exception = assertThrows(InvalidDirectionException.class, () -> board.moveItem(blackQueenRow, blackQueenCol, 4, 1));
            System.err.println("!! Exception 1. " + exception.getMessage());

            // test moving out of the board
            System.out.println();
            System.out.println("Direction 7, steps 1 from 5, 1");
            exception = assertThrows(OutOfBoardException.class, () -> board.moveItem(blackQueenRow, blackQueenCol, 7, 1));
            System.err.println("!! Exception 2. " + exception.getMessage());

            // test moving to free place to eat the white pawn
            // false = ensure turn did end after moving to place 3, 3
            System.out.println("Direction 9, Steps 2 from 5, 1");
            assertFalse(board.moveItem(blackQueenRow, blackQueenCol, 9, 2));
            assertEquals(Board.BLACK_QUEEN, board.getBoardArray()[3][3]);
            assertEquals(Board.EMPTY_PLACE, board.getBoardArray()[5][1]);
            blackQueenRow = blackQueenCol = 3;
            System.out.println(board);

            // Eat obligation rules test

            // ensure direction lead to eat obligation
            System.out.println("Direction 9, steps 1 from 5, 1");
            exception = assertThrows(EatObligationException.class, () -> board.moveItem(blackQueenRow, blackQueenCol, 9, 1));
            System.err.println("!! Exception 3. " + exception.getMessage());

            // ensure steps satisfy eat obligation
            System.out.println("Direction 3, steps 1 from 5, 1");
            exception = assertThrows(EatObligationException.class, () -> board.moveItem(blackQueenRow, blackQueenCol, 3, 1));
            System.err.println("!! Exception 4. " + exception.getMessage());

            // test eat white pawn at position 5, 5 starting from position 3, 3
            // ensure turn is ended as non more possible enchained eating
            System.out.println("Direction 3, steps 4 from 3, 3");
            assertTrue(board.moveItem(blackQueenRow, blackQueenCol, 3, 4));
            assertEquals(Board.BLACK_QUEEN, board.getBoardArray()[7][7]); // black queen is on 7, 7
            assertEquals(Board.EMPTY_PLACE, board.getBoardArray()[5][5]); // white pawn is dead
            blackQueenRow = blackQueenCol = 7;
            assertTrue(board.didGameOver());
            assertTrue(board.doPawnsColorExist(false));
        } catch (InvalidDirectionException | OutOfBoardException | EatObligationException e) {
            e.printStackTrace();
        }
    }

    @Test
    void queenChainAttackTest() {
        // add white pawn at position 4, 2
        // and so chain will be eating 4,2 to 3,3
        // then eating 5,5 to 6,6
        board.getBoardArray()[4][2] = Board.WHITE_PAWN;
        System.out.println(board);

        try {
            // eating first white pawn at 4, 2
            System.out.println("Direction 9, steps 2 from 5, 1");
            // False => ensure turn remain with same player as enchained eat obligation is present
            assertFalse(board.moveItem(blackQueenRow, blackQueenCol, 9, 2));
            assertEquals(Board.EMPTY_PLACE, board.getBoardArray()[5][1]); // old black queen location is empty
            assertEquals(Board.BLACK_QUEEN, board.getBoardArray()[3][3]); // black queen is on 3, 3
            assertEquals(Board.DEAD_PAWN, board.getBoardArray()[4][2]); // white pawn is dead
            blackQueenRow = blackQueenCol = 3;
            System.out.println(board);

            // eat next opponent at position 3, 3
            System.out.println("Direction 3, steps 3 from 3, 3");
            // true => ensure turn is ended
            assertTrue(board.moveItem(blackQueenRow, blackQueenCol, 3, 3));
            assertEquals(Board.EMPTY_PLACE, board.getBoardArray()[3][3]); // old black queen location is empty
            assertEquals(Board.BLACK_QUEEN, board.getBoardArray()[6][6]); // black queen is on 6, 6
            assertEquals(Board.EMPTY_PLACE, board.getBoardArray()[4][2]); // dead pawn is cleared
            assertEquals(Board.EMPTY_PLACE, board.getBoardArray()[3][3]); // white pawn is empty
            blackQueenRow = blackQueenCol = 6;
        } catch (InvalidDirectionException | OutOfBoardException | EatObligationException e) {
            e.printStackTrace();
        }
    }
}