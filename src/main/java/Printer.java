import cc.redberry.rings.Rational;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;

import java.io.IOException;
import java.util.Arrays;

/**
 * class related to printing to console
 */
public class Printer {
    //matrix column padding
    final static int padding = 0;

    public static void clearConsole()
    {
        if (runningFromIntelliJ()) {
            //a bunch of new line
            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        } else {
            //actual console clear for when not in IDE console
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (Exception e) {
                //do nothing
            }
        }
    }

    public static boolean runningFromIntelliJ()
    {
        //change to false on building .jar
        return true;
    }

    public static void printMatrix(Rational[][] matrix) {
        String[][] toStringMatrix = new String[matrix.length][matrix[0].length];

        //max length for each column
        int[] columnLength = new int[matrix[0].length];
        Arrays.fill(columnLength, 0);

        //insert into toStringMatrix
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                toStringMatrix[i][j] = rationalToString(matrix[i][j]);
                columnLength[j] = Math.max(columnLength[j], toStringMatrix[i][j].length() + padding);
            }
        }

        //formatted print
        for (int i = 0; i < matrix.length; i++) {
            StringBuilder out = new StringBuilder("[ ");
            out.append(" ".repeat(padding));
            for (int j = 0; j < matrix[i].length; j++) {
                out.append(toStringMatrix[i][j]);
                int spaceNeeded = columnLength[j] - toStringMatrix[i][j].length();
                out.append(" ".repeat(Math.max(0, spaceNeeded)));
                if (j < matrix[i].length - 1) out.append(", ");
            }
            out.append(" ]");
            System.out.println(out);
        }
    }

    public static String rationalToString(Rational<MultivariatePolynomial<BigInteger>> in) {
        StringBuilder output = new StringBuilder();
        String temp = in.toString();
        for (int i = 0; i < temp.length(); i++) {
            char current = temp.charAt(i);
            if (current == 'x') {
                String numString = "" + temp.charAt(i + 1);
                i++;
                if (i < temp.length() - 1) {
                    char numCheck = temp.charAt(i + 1);
                    if (Character.isDigit(numCheck)) {
                        numString += numCheck;
                        i++;
                    }
                }
                int number = Integer.parseInt(numString);
                output.append((char)('a' + number - 1));
            }
            else if (current == '*') {

            }
            else if (current == '+' || current == '-') {
                output.append(" " + current + " ");
            }
            else {
                output.append(current);
            }
        }
        return output.toString().strip();
    }
}
