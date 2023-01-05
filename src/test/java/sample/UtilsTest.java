package sample;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void testConcatWords() {
        System.out.println("concatWords");
        assertEquals("Hello, world!", Utils.concatWords(new String[]{"Hello", ", ", "world", "!"}));
    }

//    @Test
//    @Timeout(value = 10, unit = TimeUnit.NANOSECONDS)
//    @Disabled
//    void testComputeFactorial() {
//        System.out.println("computeFactorial");
//        final int factorialOf = 1 + (int) (30000 * Math.random());
//        System.out.println("computing " + factorialOf + "!");
//        System.out.println(factorialOf + "! = " + Utils.computeFactorial(factorialOf));
//    }

    @Test
    void checkExpectedException() {
        System.out.println("checkExpectedException");
        final int factorialOf = -5;
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                //code under test for throwing IllegalArgumentException
                System.out.println(factorialOf + "! = " + Utils.computeFactorial(factorialOf)));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void testWithValueSource(int argument) {
        System.out.println("hello there " + argument);
        assertTrue(argument > 0 && argument < 4);
    }

    @AfterAll
    static void afterEach() {
        System.out.println("i'm after each");
    }
}
