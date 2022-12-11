package org.ecn;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Board location data holder
 */
@Data
@NoArgsConstructor
public class BoardLocation {
    public int row;
    public int col;

    public BoardLocation(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public BoardLocation(int row, int col, int reachedByDirection) {
        this.row = row;
        this.col = col;
    }

    /**
     * Calculate direction to take to reach this point from source point given in parameter
     *
     * @param rowIndexSource the source row point
     * @param colIndexSource the source column point
     * @return direction as if source point want to reach this point using direction as defined in @{@link MoveBehavior}
     */
    public int deduceDirectionFromSource(int rowIndexSource, int colIndexSource) {
        return MoveBehavior.getDirectionFromDxDy(getCol() - colIndexSource, getRow() - rowIndexSource);
    }

    public int distanceDiagonallyFrom(int rowIndex) {
        return Math.abs(rowIndex - row);
    }
}
