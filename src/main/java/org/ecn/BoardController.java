package org.ecn;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecn.exp.EatObligationException;
import org.ecn.exp.InvalidDirectionException;
import org.ecn.exp.OutOfBoardException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class to interact with user and change the board
 */
@Data
@NoArgsConstructor
public class BoardController {

    private Board board;

    private String firstPlayerName;
    private String secondPlayerName;

    public BoardController(Board board) {
        this.board = board;
        firstPlayerName = "Player one";
        secondPlayerName = "Player two";
    }

    public BoardController(Board board, String firstPlayerName, String secondPlayerName) {
        this.board = board;
        this.firstPlayerName = firstPlayerName;
        this.secondPlayerName = secondPlayerName;
    }

    public void startGame() {
        System.out.println("Welcome players !");
        System.out.println("Assigned white color to " + firstPlayerName);
        System.out.println("Assigned black color to " + secondPlayerName);
        boolean whiteTurn = false;

        System.out.println(Board.prettyPrintLegend());
        while (!board.didGameOver()) {
            System.out.println("------------------------------------------------------");
            System.out.println("-------------------- Turn "
                    + (whiteTurn ? firstPlayerName : secondPlayerName)
                    + (whiteTurn ?
                    " - White (" + Board.WHITE_PAWN + " or " + Board.WHITE_QUEEN + ")"
                    : " - Black (" + Board.BLACK_PAWN + " or " + Board.BLACK_QUEEN + ")"
            ));
            System.out.println("------------------------------------------------------");
            playNextTurn(whiteTurn);
            whiteTurn = !whiteTurn;
        }
        System.out.println(board);
        System.out.println(board.prettyPrintWinningColor());
    }

    public void playNextTurn(boolean isWhiteTurn) {
        boolean didTurnEnd = false;
        while (!didTurnEnd) {
            List<BoardLocation> obligationAttackers = board.getAttackersList(isWhiteTurn);
            System.out.println(board);
            BoardLocation targetItem;
            if(obligationAttackers.size() != 0) {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!! Time to eat some pawns!!!!!!!!!!!!!!!!!!!!!!!");
            }
            if (obligationAttackers.isEmpty()) {
                targetItem = pickTargetPion(isWhiteTurn);
//                targetItem = pickBoardLocationFromList(board.getPawnsList(isWhiteTurn), "Choose Pawn for your next turn");
            } else if (obligationAttackers.size() == 1) {
                System.out.println("Eat obligation rule, automatically chosen target item.");
                targetItem = obligationAttackers.get(0);
            } else {
                targetItem = pickBoardLocationFromList(obligationAttackers, "Choose Pawn for your next turn");
            }
            try {
                didTurnEnd = moveItem(targetItem.getRow(), targetItem.getCol());
            } catch (InvalidDirectionException | OutOfBoardException | EatObligationException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Ask user to pick Pion item from the board such that he own the item.
     *
     * @param isWhitePawn <code>true</code> -> restrict to pick white pawn <br/>
     *                    <code>false</code> -> restrict to pick black pawn
     * @return 2d point containing row index, and column index in the board owned by selected user
     */
    public BoardLocation pickTargetPion(boolean isWhitePawn) {
        System.out.println("Pick " + (isWhitePawn ?
                "White (" + Board.WHITE_PAWN + " or " + Board.WHITE_QUEEN + ")"
                : "Black (" + Board.BLACK_PAWN + " or " + Board.BLACK_QUEEN + ")"
        ) + " from board location to move:");
        int rowIndex;
        int colIndex;
        boolean isCaseOwnedByPlayer;
        do {
            rowIndex = InputIntegerUtils.getBoundedInteger(1, board.getTailleBoard(), "Chosen Row");
            colIndex = InputIntegerUtils.getBoundedInteger(1, board.getTailleBoard(), "Chosen Column");
            isCaseOwnedByPlayer = (isWhitePawn && board.isWhitePion(rowIndex, colIndex))
                    || (!isWhitePawn && board.isBlackPion(rowIndex, colIndex));
            if (!isCaseOwnedByPlayer)
                System.out.println("You do not have the right to chose this item!");
        } while (!isCaseOwnedByPlayer);

        return new BoardLocation(rowIndex, colIndex);
    }

    public BoardLocation pickBoardLocationFromList(List<BoardLocation> forcedList, String header) {
        StringBuilder integerMeaning = new StringBuilder();
        integerMeaning.append(header).append("\n");
        int i = 1;
        for (BoardLocation boardLocation : forcedList) {
            integerMeaning.append(i)
                    .append(" - location at (row, col) : (")
                    .append(boardLocation.getRow())
                    .append(", ")
                    .append(boardLocation.getCol())
                    .append(")\n");
            i++;
        }
        integerMeaning.append("---------------------------------\n");
        integerMeaning.append("Chosen target");
        int chosenBoardLocation = InputIntegerUtils.getBoundedInteger(1, forcedList.size(), integerMeaning.toString()) - 1;

        return forcedList.get(chosenBoardLocation);
    }

    /**
     * Ask user to move item given in parameter as input diagonal direction as input number of steps.
     * <pre>
     *     Direction is specified upon number selection 1, 3, 7 or 9 as follows
     *     7 - 9
     *     - P -
     *     1 - 3
     *
     *     After choosing direction, user has to input the steps to move if it was a Queen item.
     * </pre>
     *
     * @param rowIndex the target row item to move
     * @param colIndex the target column item to move
     * @return <code>true</code> if move was successful and turn swap to other player <br/>
     * <code>false</code> if move wasn't successful (ex.occupied case) or player is forced to continue his turn (such as Queen enchained eating)
     */
    public boolean moveItem(Integer rowIndex, Integer colIndex)
            throws InvalidDirectionException, OutOfBoardException, EatObligationException {
        String integerMeaning = "Target item (row, col) = (" + rowIndex + ", " + colIndex + ")\n" +
                "Choose a diagonal direction from 1, 3, 7 or 9 as follow : " + "\n" +
                "7\t-\t9" + "\n" +
                "-\tP\t-" + "\n" +
                "1\t-\t3" + "\n" +
                "Chosen Direction: ";
        int moveBehaviorDirection = InputIntegerUtils.getIntegerInList(Board.ALLOWED_DIRECTIONS, integerMeaning);
        int steps = board.isNormalPawn(rowIndex, colIndex) ? 1 : InputIntegerUtils.getBoundedInteger(1, board.getTailleBoard(), "Queen Steps");
        return board.moveItem(rowIndex, colIndex, moveBehaviorDirection, steps);
    }
}
