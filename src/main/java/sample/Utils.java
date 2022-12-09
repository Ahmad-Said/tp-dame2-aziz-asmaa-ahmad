/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sample;

import java.lang.reflect.Method;
import java.math.BigInteger;

/**
 *
 * @author  nb
 */
public class Utils {

    private Utils() { }


    public static String concatWords(String[] words) {
        StringBuilder buf = new StringBuilder();
        for (String word : words) {
            buf.append(word);
        }
        return buf.toString();
    }

    public static String computeFactorial(int number) 
                                               throws IllegalArgumentException {
        if (number < 1) {
            throw new IllegalArgumentException("zero or negative parameter (" + number + ')');
        }

        BigInteger factorial = new BigInteger("1");
        for (int i = 2; i <= number; i++) {
            factorial = factorial.multiply(new BigInteger(String.valueOf(i)));
        }
        return factorial.toString();
    }

}
