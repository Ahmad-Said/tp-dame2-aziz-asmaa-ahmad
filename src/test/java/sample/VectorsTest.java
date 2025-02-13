package sample;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class VectorsTest {
    @Test
    void testScalarMultiplication() {
        System.out.println("scalarMultiplication");
        assertEquals(  0, Vectors.scalarMultiplication(new int[] { 0, 0}, new int[] { 0, 0}));
        assertEquals( 39, Vectors.scalarMultiplication(new int[] { 3, 4}, new int[] { 5, 6}));
        assertEquals(-39, Vectors.scalarMultiplication(new int[] {-3, 4}, new int[] { 5,-6}));
        assertEquals(  0, Vectors.scalarMultiplication(new int[] { 5, 9}, new int[] {-9, 5}));
        assertEquals(100, Vectors.scalarMultiplication(new int[] { 6, 8}, new int[] { 6, 8}));
    }

    @Test
    void testEqual() {
        System.out.println("isVectorsEquals");
        assertTrue(Vectors.isVectorsEquals(new int[] {}, new int[] {}));
        assertTrue(Vectors.isVectorsEquals(new int[] {0}, new int[] {0}));
        assertTrue(Vectors.isVectorsEquals(new int[] {0, 0}, new int[] {0, 0}));
        assertTrue(Vectors.isVectorsEquals(new int[] {0, 0, 0}, new int[] {0, 0, 0}));
        assertTrue(Vectors.isVectorsEquals(new int[] {5, 6, 7}, new int[] {5, 6, 7}));

        assertFalse(Vectors.isVectorsEquals(new int[] {}, new int[] {0}));
        assertFalse(Vectors.isVectorsEquals(new int[] {0}, new int[] {0, 0}));
        assertFalse(Vectors.isVectorsEquals(new int[] {0, 0}, new int[] {0, 0, 0}));
        assertFalse(Vectors.isVectorsEquals(new int[] {0, 0, 0}, new int[] {0, 0}));
        assertFalse(Vectors.isVectorsEquals(new int[] {0, 0}, new int[] {0}));
        assertFalse(Vectors.isVectorsEquals(new int[] {0}, new int[] {}));

        assertFalse(Vectors.isVectorsEquals(new int[] {0, 0, 0}, new int[] {0, 0, 1}));
        assertFalse(Vectors.isVectorsEquals(new int[] {0, 0, 0}, new int[] {0, 1, 0}));
        assertFalse(Vectors.isVectorsEquals(new int[] {0, 0, 0}, new int[] {1, 0, 0}));
        assertFalse(Vectors.isVectorsEquals(new int[] {0, 0, 1}, new int[] {0, 0, 3}));
    }

}