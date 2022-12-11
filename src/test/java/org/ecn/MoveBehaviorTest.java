package org.ecn;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveBehaviorTest {

    @Test
    void getDirectionFromDxDy() {
        assertEquals(1, MoveBehavior.getDirectionFromDxDy(-1, 1));
        assertEquals(2, MoveBehavior.getDirectionFromDxDy(0, 1));
        assertEquals(3, MoveBehavior.getDirectionFromDxDy(1, 1));
        assertEquals(4, MoveBehavior.getDirectionFromDxDy(-1, 0));
        assertEquals(5, MoveBehavior.getDirectionFromDxDy(0, 0));
        assertEquals(6, MoveBehavior.getDirectionFromDxDy(1, 0));
        assertEquals(7, MoveBehavior.getDirectionFromDxDy(-1, -1));
        assertEquals(8, MoveBehavior.getDirectionFromDxDy(0, -1));
        assertEquals(9, MoveBehavior.getDirectionFromDxDy(1, -1));
    }

    @Test
    void getDirectionFromDxDyMultiplied() {
        int multXWith = 5;
        int multYWith = 8;
        assertEquals(1, MoveBehavior.getDirectionFromDxDy(multXWith * -1, 1 * multYWith));
        assertEquals(2, MoveBehavior.getDirectionFromDxDy(multXWith * 0, 1 * multYWith));
        assertEquals(3, MoveBehavior.getDirectionFromDxDy(multXWith * 1, 1 * multYWith));
        assertEquals(4, MoveBehavior.getDirectionFromDxDy(multXWith * -1, 0 * multYWith));
        assertEquals(5, MoveBehavior.getDirectionFromDxDy(multXWith * 0, 0 * multYWith));
        assertEquals(6, MoveBehavior.getDirectionFromDxDy(multXWith * 1, 0 * multYWith));
        assertEquals(7, MoveBehavior.getDirectionFromDxDy(multXWith * -1, -1 * multYWith));
        assertEquals(8, MoveBehavior.getDirectionFromDxDy(multXWith * 0, -1 * multYWith));
        assertEquals(9, MoveBehavior.getDirectionFromDxDy(multXWith * 1, -1 * multYWith));
    }
}