package org.ecn;

import java.util.Scanner;

public class InterfaceUtils {
    public static Integer getIndexChoice(int arraySize, String indexMeaning) {
        if(arraySize <= 0){
            return null;
        }
        Scanner in = new Scanner(System.in);
        boolean isValidChoice = false;
        String choice = "", st;
        Integer indexChoice = null, i = 1;
        do {
            System.out.print(indexMeaning + " [1-" + arraySize + "]: ");
            choice = in.next();
            for (i = 1; i <= arraySize; i++) {
                st = "" + i;
                if (choice.equals(st)) {
                    isValidChoice = true;
                    indexChoice = i - 1;
                }
            }
        } while (!isValidChoice);
        return indexChoice;
    }
}
