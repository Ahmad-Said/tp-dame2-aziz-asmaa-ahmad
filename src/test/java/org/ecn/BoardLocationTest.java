package org.ecn;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardLocationTest {

    @Test
    void getDirectionFromSource() {
        BoardLocation target = new BoardLocation(5,5);
        assertEquals(1, target.deduceDirectionFromSource(3, 7));
        assertEquals(3, target.deduceDirectionFromSource(3, 3));
        assertEquals(7, target.deduceDirectionFromSource(7, 7));
        assertEquals(9, target.deduceDirectionFromSource(7, 3));
    }

    @Test
    void testEquals() {
        assertEquals(new BoardLocation(1, 2), new BoardLocation(1, 2));
        assertNotEquals(new BoardLocation(3, 1), new BoardLocation(1, 2));
    }

    @Test
    void distanceFrom() {
        // other point location can be at position (4, 5) or (4, 1)
        assertEquals(2, new BoardLocation(2,3).distanceDiagonallyFrom(4));

        assertEquals(3, new BoardLocation(5, 5).distanceDiagonallyFrom(8));

    }
}