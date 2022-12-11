package org.ecn;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class InputIntegerUtils {

    /**
     * Callback interface used to satisfy integer condition
     */
    interface IntegerCondition {
        boolean isValid(int i);
    }

    /**
     * Ask user to enter integer bounded by min and max value inclusive.
     *
     * Usage example for index array, use 0 as minValue, and array length - 1 as maxValue
     *
     * @param minValue the minimum value that can be returned
     * @param maxValue the maximum value that can be returned
     * @param integerMeaning the business meaning of the integer input by the user
     * @return Integer bounded between minimum and maximum value inclusive
     */
    public static int getBoundedInteger(int minValue, int maxValue, String integerMeaning) {
        return getConditionalInteger(i -> i >= minValue && i <= maxValue, integerMeaning + " [" + minValue + "-" + maxValue+"]: ");
    }

    /**
     * Ask user to enter integer from integer list.
     *
     * It is recommended to include the description of each integer in integerMeaning parameter
     * @param integerList the list of integer values that can be returned
     * @param integerMeaning the meaning of integer input by user
     * @return integer from integer list sent by parameter
     */
    public static int getIntegerInList(List<Integer> integerList, String integerMeaning) {
        HashSet<Integer> integerHashSet = new HashSet<>(integerList);
        return getConditionalInteger(integerHashSet::contains, integerMeaning);
    }

    /**
     * Ask user for integer under condition specified by parameter
     *
     * @param integerCondition the condition to be met by integer input
     * @param integerMeaning the integer meaning asked for
     * @return an integer meeting the condition specified in parameter
     */
    public static int getConditionalInteger(IntegerCondition integerCondition, String integerMeaning) {
        Scanner in = new Scanner(System.in);
        boolean isValidChoice = false;
        String inputChoice = "";
        Integer parsedChoice = null;
        do {
            System.out.print(integerMeaning);
            inputChoice = in.next();
            try {
                parsedChoice = Integer.parseInt(inputChoice);
                if (integerCondition != null && integerCondition.isValid(parsedChoice)) {
                    isValidChoice = true;
                } else {
                    System.out.println("Number does not meet asked condition!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format!");
            }
        } while (!isValidChoice);
        return parsedChoice;
    }
}
