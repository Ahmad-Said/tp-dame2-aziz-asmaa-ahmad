package org.ecn;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecn.exp.EatObligationException;
import org.ecn.exp.InvalidDirectionException;
import org.ecn.exp.OutOfBoardException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * For the simplicity of the project, integer are used to designate Pawns,
 * otherwise create a class for each one with right inheritance
 * Empty place => 0 printed as dash -
 * Black pawn => 1
 * Black queen => 2
 * White pawn => 3
 * White queen => 4
 */
@Data
@NoArgsConstructor
public class Board {


    public static final int EMPTY_PLACE = 0;
    public static final int BLACK_PAWN = 1;
    public static final int BLACK_QUEEN = 2;
    public static final int WHITE_PAWN = 3;
    public static final int WHITE_QUEEN = 4;

    /**
     * When queen eat multiple pawn with chain attack, eaten pawn are not considered empty until next turn
     * check wiki <a href="https://fr.wikipedia.org/wiki/Dames#La_prise">Game eat rules</a>
     */
    public static final int DEAD_PAWN = -1;

    /**
     * Allowed direction are only diagonals
     *
     * @see MoveBehavior
     */
    public static final List<Integer> ALLOWED_DIRECTIONS = Collections.unmodifiableList(Arrays.asList(1, 3, 7, 9));

    private int[][] boardArray;

    private int tailleBoard;

    public Board(int tailleBoard) {
        this.tailleBoard = tailleBoard;
        initializeBoard();
    }

    /**
     * Initialize a board having size {@link #getTailleBoard()}
     * Black pawn are placed on top, and white pawn are placed on bottom.
     */
    private void initializeBoard() {
        boardArray = new int[tailleBoard + 1][tailleBoard + 1];
        // i = 0 and j = 0 are discarded to have x/y location same as counting from 1
        // so we can iterate either on <= tailleBoard or on < board.length
        for (int i = 1; i < boardArray.length; i++) {
            for (int j = 1; j < boardArray[i].length; j++) {
                if (i <= tailleBoard / 2 - 1 && i % 2 != j % 2) {
                    boardArray[i][j] = BLACK_PAWN;
                }
                if (i >= tailleBoard / 2 + 2 && i % 2 != j % 2) {
                    boardArray[i][j] = WHITE_PAWN;
                }
            }
        }
    }


    /**
     * The main function of playing the game, this function move an item at given position (rowIndex, colIndex)
     * in given direction for x steps.
     *
     * <pre>
     * While moving the item will eat opponent Pawn in its way, also it will log action happened in order.
     *
     * Game rules:
     * <ol>
     *     <li> If the new position is occupied by another pawn, move does not happen and turn remain with the player
     *     </li>
     *     <li> If the pawn has an obligation to eat then pawn cannot be moved other than this obligation,
     *          otherwise a warning log is printed and turn remain with the player till he chose the right new position
     *     </li>
     *     <li> If the pawn has multiple possibilities to eat then pawn cannot be moved other than one of these possibilities,
     *          otherwise a warning log is printed and turn remain with the player till he chose the right new position
     *     </li>
     *     <li>
     *         Any Pawn (normal or queen) that move and eat an opponent Pawn and still have the possibility to eat another pawn,
     *         then the turn remain with the player till he finish all his eating chain.
     *     </li>
     * </ol>
     * </pre>
     *
     * @param rowIndex              the target row item to move
     * @param colIndex              the target column item to move
     * @param moveBehaviorDirection direction as defined in class {@link MoveBehavior}
     * @param steps                 number of steps to perform as Queen, if steps exceed the limit then maximum allowed steps is made.<br/>
     *                              Note: this argument is discarded in case of normal pions
     * @return <code>true</code> if turn of current player end i.e. swap to other player <br/>
     * <code>false</code> if move wasn't successful (ex.occupied case) or player is forced to continue his turn (such as enchained eating)
     */
    public boolean moveItem(int rowIndex, int colIndex, int moveBehaviorDirection, int steps)
            throws InvalidDirectionException, OutOfBoardException, EatObligationException {
        // cannot only move alive pawn
        if (isEmptyPlace(rowIndex, colIndex) || isDeadPawn(rowIndex, colIndex))
            // we can throw an exception also
            return false;
        boolean isNormalPawn = isNormalPawn(rowIndex, colIndex);
        if (isNormalPawn) return moveAsNormalPawn(rowIndex, colIndex, moveBehaviorDirection);
        else return moveAsQueenPawn(rowIndex, colIndex, moveBehaviorDirection, steps);
    }

    /**
     * @param rowIndex              the target row item to move
     * @param colIndex              the target column item to move
     * @param moveBehaviorDirection direction as defined in class {@link MoveBehavior}
     * @param steps                 number of steps to perform, if steps exceed the limit then maximum allowed steps is made
     * @return <code>true</code> if turn of current player end i.e. swap to other player <br/>
     * <code>false</code> if move wasn't successful (ex.occupied case) or player is forced to continue his turn (such as enchained eating)
     */
    private boolean moveAsQueenPawn(final int rowIndex, final int colIndex, int moveBehaviorDirection, int steps)
            throws InvalidDirectionException, EatObligationException, OutOfBoardException {

        // check if direction is allowed and throw exception if not
        checkDirectionIfAllowed(moveBehaviorDirection);

        int moveDrow = MoveBehavior.DIRECTION_TO_DY.get(moveBehaviorDirection);
        int moveDcol = MoveBehavior.DIRECTION_TO_DX.get(moveBehaviorDirection);
        int pawnToMove = boardArray[rowIndex][colIndex];
        List<BoardLocation> attackList = getAttackPossibilitiesList(rowIndex, colIndex);
        BoardLocation mustBeEaten = attackList.stream().filter(t -> t.deduceDirectionFromSource(rowIndex, colIndex) == moveBehaviorDirection)
                .findFirst().orElse(null);

        // if attack list isn't empty and direction does not lead to any of them => throw new eat exception
        if (!attackList.isEmpty() && mustBeEaten == null) {
            throw new EatObligationException("Eat obligation exist at positions " + attackList
                    + "\nSuggested direction for attacks: " + attackList.stream().map(t -> t.deduceDirectionFromSource(rowIndex, colIndex)).collect(Collectors.toList()));
        }

        // eat obligation exist but steps cannot reach eaten item
        if (mustBeEaten != null && steps <= mustBeEaten.distanceDiagonallyFrom(rowIndex)) {
            throw new EatObligationException("Cannot reach eat obligation at position " + mustBeEaten
                    + " - insuffisant steps " + steps
                    + "\nSuggested minimum steps: " + (mustBeEaten.distanceDiagonallyFrom(rowIndex) + 1));
        }
        int remainingSteps = steps;
        int newRowIndex = rowIndex;
        int newColIndex = colIndex;
        if (mustBeEaten != null) {
            int minimumJumpedSteps = mustBeEaten.distanceDiagonallyFrom(rowIndex) + 1;
            remainingSteps -= minimumJumpedSteps;
            newRowIndex += minimumJumpedSteps * moveDrow;
            newColIndex += minimumJumpedSteps * moveDcol;
            System.out.println("Opponent eaten at position [" + mustBeEaten.row + ", " + mustBeEaten.col + "] !");
            boardArray[mustBeEaten.row][mustBeEaten.col] = DEAD_PAWN;
        }
        // walk with remaining steps as max as possible
        for (int i = 0; i < remainingSteps; i++) {
            int testNextRowIndex = newRowIndex + moveDrow;
            int testNextColIndex = newColIndex + moveDcol;
            // break steps new location is out of boards, or it is not empty
            if (isLocationOutOfBoard(testNextRowIndex, testNextColIndex)) {
                String errorMessage = "Place [" + testNextRowIndex + ", " + testNextColIndex + "] is out of the board!";
                if (i == 0) {
                    // step was first move => throw error
                    throw new OutOfBoardException(errorMessage);
                } else {
                    // step is for cumulative move => log warning
                    System.err.println(errorMessage);
                }
                break;
            }
            if (!isEmptyPlace(testNextRowIndex, testNextColIndex)) {
                System.err.println("Cannot step further than [" + testNextRowIndex + ", " + testNextColIndex + "] as position isn't available!");
                break;
            }
            newRowIndex = testNextRowIndex;
            newColIndex = testNextColIndex;
            i--;
            remainingSteps--;
        }
        if (remainingSteps != 0) {
            System.err.println("=> Discarding remaining " + remainingSteps + " steps.");
        }
        System.out.println("Moved to new Place [" + newRowIndex + ", " + newColIndex + "]!");
        boardArray[rowIndex][colIndex] = EMPTY_PLACE;
        boardArray[newRowIndex][newColIndex] = pawnToMove;
        // turn end if there is no more eat obligations at the new positions
        boolean didTurnEnd = getAttackPossibilitiesList(newRowIndex, newColIndex).isEmpty();

        if (didTurnEnd) {
            // clear dead pawns if turn is ended
            clearDeadPawns();
        }
        return didTurnEnd;
    }

    /**
     * Handle move of normal Pawn in 2 cases:
     * <ol>
     *     <li>Move to empty place</li>
     *     <li>Attack an enemy in adjacent place</li>
     * </ol>
     * <p>
     * Effects taken in consideration: Promotion to Queen.
     *
     * @param rowIndex              the target row item to move
     * @param colIndex              the target column item to move
     * @param moveBehaviorDirection direction as defined in class {@link MoveBehavior}
     * @return <code>true</code> if turn of current player end i.e. swap to other player <br/>
     * <code>false</code> if move wasn't successful (ex.occupied case) or player is forced to continue his turn (enchained eating)
     */
    private boolean moveAsNormalPawn(int rowIndex, int colIndex, int moveBehaviorDirection)
            throws OutOfBoardException, EatObligationException, InvalidDirectionException {
        // verify that direction is diagonal
        checkDirectionIfAllowed(moveBehaviorDirection);

        int moveDrow = MoveBehavior.DIRECTION_TO_DY.get(moveBehaviorDirection);
        int moveDcol = MoveBehavior.DIRECTION_TO_DX.get(moveBehaviorDirection);
        int pawnToMove = boardArray[rowIndex][colIndex];
        int newRowIndex = rowIndex + moveDrow;
        int newColIndex = colIndex + moveDcol;

        // verify if new position is out of board
        checkLocationIfOutOfBoard(newRowIndex, newColIndex);

        List<BoardLocation> attackList = getAttackPossibilitiesList(rowIndex, colIndex);

        // check eat obligation rule
        if (!attackList.isEmpty() && !attackList.contains(new BoardLocation(newRowIndex, newColIndex))) {
            // move was discarding eat obligation rule, turn remain with same player
            // controller should help user avoiding this exception by suggesting pawns to eat
            throw new EatObligationException("Eat obligation exist at positions " + attackList
                    + "\nSuggested direction for attacks: " + attackList.stream().map(t -> t.deduceDirectionFromSource(rowIndex, colIndex)).collect(Collectors.toList()));
        }


        boolean doEndTurn = false;
        if (isEmptyPlace(newRowIndex, newColIndex)) {
            System.out.println("Moved to new Place [" + newRowIndex + ", " + newColIndex + "]!");
            boardArray[rowIndex][colIndex] = EMPTY_PLACE;
            boardArray[newRowIndex][newColIndex] = pawnToMove;
            // successful move to empty place => turn go to next player
            doEndTurn = true;
        } else {
            // position is occupied by same player => cannot move
            if (isOccupiedBySamePlayer(rowIndex, colIndex, newRowIndex, newColIndex)) {
                // note all print on System.err can be elevated as defined exception,
                // but for the simplicity of project we print the error in the standard error output stream,
                // and we keep the turn with same player
                System.err.println("Place [" + newRowIndex + ", " + colIndex + "] already occupied!");
                doEndTurn = false;
            } else if (isOccupiedByOpponentPlayer(rowIndex, colIndex, newRowIndex, newColIndex)) {
                // position is occupied by opponent player => try to attack
                if (attackList.contains(new BoardLocation(newRowIndex, newColIndex))) {
                    System.out.println("Opponent eaten at position [" + newRowIndex + ", " + newColIndex + "] !");
                    boardArray[newRowIndex][newColIndex] = DEAD_PAWN;
                    boardArray[rowIndex][colIndex] = EMPTY_PLACE;
                    // jump to next position over eaten Pawn
                    newRowIndex += moveDrow;
                    newColIndex += moveDcol;
                    boardArray[newRowIndex][newColIndex] = pawnToMove;
                    // turn of player depend on attack chain: if no attack is possible then the turn go to next player
                    // otherwise current player have to choose the next attack position. (controller should notify user of possible attacks)
                    doEndTurn = getAttackPossibilitiesList(newRowIndex, newColIndex).isEmpty();
                } else {
                    // position is occupied by opponent player but cannot attack
                    System.err.println("Cannot Attack [" + newRowIndex + ", " + colIndex + "] as next position isn't available!");
                    doEndTurn = false;
                }
            }
        }
        if (doEndTurn) {
            clearDeadPawns();
            // promotion to Queen
            if (isWhitePion(newRowIndex, newColIndex) && newRowIndex == 1) {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!! White Queen !!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("Congrats! Pawn promoted to White Queen!");
                boardArray[newRowIndex][newColIndex] = WHITE_QUEEN;
            } else if (isBlackPion(newRowIndex, newColIndex) && newRowIndex == tailleBoard) {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!! Black Queen !!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("Congrats! Pawn promoted to Black Queen!");
                boardArray[newRowIndex][newColIndex] = BLACK_QUEEN;
            }
        }
        return doEndTurn;
    }


    /**
     * Check if direction is allowed
     *
     * @param directionBehavior the direction as defined in {@link MoveBehavior}
     * @throws InvalidDirectionException if direction isn't allowed
     * @see #ALLOWED_DIRECTIONS
     */
    public void checkDirectionIfAllowed(int directionBehavior) throws InvalidDirectionException {
        if (!ALLOWED_DIRECTIONS.contains(directionBehavior)) {
            throw new InvalidDirectionException("Direction [" + directionBehavior + "] is not allowed" +
                    " - Move can only be in diagonal direction " + ALLOWED_DIRECTIONS);
        }
    }

    /**
     * Check if location at given row, column is a valid location
     *
     * @param rowIndex row location
     * @param colIndex column location
     * @throws OutOfBoardException if location is out of the board
     */
    public void checkLocationIfOutOfBoard(int rowIndex, int colIndex)
            throws OutOfBoardException {
        // check if position is out of the board
        if (isLocationOutOfBoard(rowIndex, colIndex)) {
            throw new OutOfBoardException("Place [" + rowIndex + ", " + colIndex + "] is out of the board!");
        }
    }

    public boolean isLocationOutOfBoard(int rowIndex, int colIndex) {
        return rowIndex < 1 || rowIndex > tailleBoard || colIndex < 1 || colIndex > tailleBoard;
    }


    /**
     * Iterate over all pawns of given pawns color (white or black), and collect these pawns into list
     *
     * @param isUsingWhitePawns <code>true</code> to return list of white pawns <br/>
     *                          <code>false</code> to return list of black pawns
     * @return list of pawns matching color in argument
     */
    public List<BoardLocation> getPawnsList(boolean isUsingWhitePawns) {
        List<BoardLocation> attackersList = new ArrayList<>();
        for (int i = 0; i < boardArray.length; i++) {
            for (int j = 0; j < boardArray[i].length; j++) {
                if (isUsingWhitePawns) {
                    if (isWhitePion(i, j))
                        attackersList.add(new BoardLocation(i, j));
                } else {
                    if (isBlackPion(i, j))
                        attackersList.add(new BoardLocation(i, j));
                }
            }
        }
        return attackersList;
    }

    /**
     * Iterate over all pawns of given pawns color (white or black), and return the pawns that have possible attack.
     * At the next turn one of these pawns must be chosen to play to ensure eat obligation rule
     *
     * @param isUsingWhitePawns <code>true</code> to return list of white pawns attackers <br/>
     *                          <code>false</code> to return list of black pawns attackers
     * @return list of pawns that can attack enemy pawns
     */
    public List<BoardLocation> getAttackersList(boolean isUsingWhitePawns) {
        List<BoardLocation> attackersList = new ArrayList<>();
        for (int i = 0; i < boardArray.length; i++) {
            for (int j = 0; j < boardArray[i].length; j++) {
                if (isUsingWhitePawns) {
                    if (isWhitePion(i, j) && !getAttackPossibilitiesList(i, j).isEmpty())
                        attackersList.add(new BoardLocation(i, j));
                } else {
                    if (isBlackPion(i, j) && !getAttackPossibilitiesList(i, j).isEmpty())
                        attackersList.add(new BoardLocation(i, j));
                }
            }
        }
        return attackersList;
    }

    /**
     * Calculate list of possible attack from given location
     *
     * @param rowIndex row location
     * @param colIndex column location
     * @return list of all opponent pawns that can be attacked from given location
     */
    public List<BoardLocation> getAttackPossibilitiesList(int rowIndex, int colIndex) {
        // improve performance by skipping non pawns locations
        if (!isWhitePion(rowIndex, colIndex) && !isBlackPion(rowIndex, colIndex))
            return new ArrayList<>();

        List<BoardLocation> attackList = new ArrayList<>();
        for (Integer direction : ALLOWED_DIRECTIONS) {
            int moveDrow = MoveBehavior.DIRECTION_TO_DY.get(direction);
            int moveDcol = MoveBehavior.DIRECTION_TO_DX.get(direction);
            int steps = isNormalPawn(rowIndex, colIndex) ? 1 : tailleBoard;
            for (int i = 1; i <= steps; i++) {
                int eatenRowIndex = rowIndex + i * moveDrow;
                int eatenColIndex = colIndex + i * moveDcol;
                // after eating an item, at least next position must be free to be occupied by attacker
                int newRowIndex = eatenRowIndex + moveDrow;
                int newColIndex = eatenColIndex + moveDcol;
                // break steps new location is out of boards
                if (isLocationOutOfBoard(newRowIndex, newColIndex))
                    break;
                if (!isEmptyPlace(eatenRowIndex, eatenColIndex)) {
                    // a blocking place encountered => we have 2 cases:
                    // the pawn is opponent and can jump over it => we add enemy as possible attack and break the loop
                    // otherwise the pawn is opponent but can't jump over it, a dead pawn, a teammate pawn => do nothing and break the loop.
                    if (isOccupiedByOpponentPlayer(rowIndex, colIndex, eatenRowIndex, eatenColIndex) && isEmptyPlace(newRowIndex, newColIndex)) {
                        attackList.add(new BoardLocation(eatenRowIndex, eatenColIndex, direction));
                        // player eat one pawn in each direction maximum in one turn
                        // for chained attack he continues his turn managed by move functions
                    }
                    break;
                }
            }
        }
        return attackList;
    }

    private void clearDeadPawns() {
        for (int i = 0; i < boardArray.length; i++) {
            for (int j = 0; j < boardArray[i].length; j++) {
                if (boardArray[i][j] == DEAD_PAWN)
                    boardArray[i][j] = EMPTY_PLACE;
            }
        }
    }

    public void clearBoard() {
        boardArray = new int[tailleBoard + 1][tailleBoard + 1];
    }

    /**
     * Specify if only one color exist on the board <br/>
     *
     * @return <code>true</code> Game is over and dominated by one color <br/>
     * <code>false</code> Game isn't over and both color exist on board.
     * In such case, to know wining color use {@link #doPawnsColorExist(boolean)}
     */
    public boolean didGameOver() {
        boolean whiteExist = doPawnsColorExist(true);
        boolean blackExist = doPawnsColorExist(false);
        return !whiteExist || !blackExist;
    }

    /**
     * Check if color specified in argument have any alive pawns on the board
     *
     * @param isWhiteColor <code>true</code> check for white color
     *                     <code>false</code> check for black color
     * @return <code>true</code>Pawn with specified color exist on the board<br/>
     * <code>false</code> No pawns with specified color exist on the board
     */
    public boolean doPawnsColorExist(boolean isWhiteColor) {
        boolean colorExist = false;
        for (int i = 0; i < boardArray.length; i++) {
            for (int j = 0; j < boardArray[i].length; j++) {
                if (isWhiteColor) {
                    if (isWhitePion(i, j))
                        colorExist = true;
                } else {
                    if (isBlackPion(i, j))
                        colorExist = true;
                }
            }
            if (colorExist)
                break;
        }
        return colorExist;
    }

    public String prettyPrintWinningColor() {
        if (!didGameOver()) {
            return "No wining colors, both color exist on map !";
        }
        if (doPawnsColorExist(true)) {
            return "White pawns win!";
        }else {
            return "Black pawns win!";
        }
    }

    public boolean isOccupiedBySamePlayer(int row1, int col1, int row2, int col2) {
        return (isWhitePion(row1, col1) && isWhitePion(row2, col2))
                || (isBlackPion(row1, col1) && isBlackPion(row2, col2));
    }

    public boolean isOccupiedByOpponentPlayer(int row1, int col1, int row2, int col2) {
        return (isWhitePion(row1, col1) && isBlackPion(row2, col2))
                || (isBlackPion(row1, col1) && isWhitePion(row2, col2));
    }

    public boolean isEmptyPlace(int rowIndex, int colIndex) {
        return boardArray[rowIndex][colIndex] == EMPTY_PLACE;
    }

    public boolean isDeadPawn(int rowIndex, int colIndex) {
        return boardArray[rowIndex][colIndex] == DEAD_PAWN;
    }

    public boolean isWhitePion(int rowIndex, int colIndex) {
        return boardArray[rowIndex][colIndex] == WHITE_PAWN || boardArray[rowIndex][colIndex] == WHITE_QUEEN;
    }

    public boolean isBlackPion(int rowIndex, int colIndex) {
        return boardArray[rowIndex][colIndex] == BLACK_PAWN || boardArray[rowIndex][colIndex] == BLACK_QUEEN;
    }

    public boolean isQueenPawn(int rowIndex, int colIndex) {
        return boardArray[rowIndex][colIndex] == BLACK_QUEEN || boardArray[rowIndex][colIndex] == WHITE_QUEEN;
    }

    public boolean isNormalPawn(int rowIndex, int colIndex) {
        return boardArray[rowIndex][colIndex] == BLACK_PAWN || boardArray[rowIndex][colIndex] == WHITE_PAWN;
    }

    public static String prettyPrintLegend() {
        String st = "* Case vide => -" + "\n" +
                "* Cadavre  => x" + "\n" +
                "* Pion noir  => " + BLACK_PAWN + "\n" +
                "* Dame noire => " + BLACK_QUEEN + "\n" +
                "* Pion blanc => " + WHITE_PAWN + "\n" +
                "* Dame blanche => " + WHITE_QUEEN + "\n";
        return st;
    }

    @Override
    public String toString() {
        StringBuilder st = new StringBuilder(boardArray.length * boardArray[0].length);
        // append first line index
        st.append("Board: \n");
        st.append("r\\c ");
        for (int i = 1; i < boardArray.length; i++) {
            st.append(i % 10).append(" ");
        }
        st.append("\n");
        for (int i = 1; i < boardArray.length; i++) {
            st.append(i % 10 + "   ");
            for (int j = 1; j < boardArray[i].length; j++) {
                switch (boardArray[i][j]) {
                    case EMPTY_PLACE:
                        st.append("-");
                        break;
                    case DEAD_PAWN:
                        st.append("x");
                        break;
                    default:
                        st.append(boardArray[i][j]);
                }
                if (j != boardArray[i].length - 1) {
                    st.append(" ");
                }
            }
            st.append("\n");
        }
        return st.toString();
    }
}
