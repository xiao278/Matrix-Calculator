import cc.redberry.rings.Rational;
import cc.redberry.rings.Rationals;
import cc.redberry.rings.Rings;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.io.Coder;
import cc.redberry.rings.poly.MultivariateRing;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;

import java.util.Arrays;
import java.util.Scanner;

import static cc.redberry.rings.Rings.Frac;
import static cc.redberry.rings.Rings.Z;

public class EROCalcProgram {
    final static int padding = 0;
    public static void main(String[] args) throws Exception {
        //[row][col]
        Rational<MultivariatePolynomial<BigInteger>>[][] matrix;
        int row;
        int col;
        Scanner s = new Scanner(System.in);
        clearConsole();
        String[] allLetters = new String[26];
        for (int i = 0; i < 26; i++) {
            allLetters[i] = "" + (char)('a' + i);
        }

        MultivariateRing<MultivariatePolynomial<BigInteger>> ring = Rings.MultivariateRing(26, Z);
        Rationals<MultivariatePolynomial<BigInteger>> field = Frac(ring);
        Coder<Rational<MultivariatePolynomial<BigInteger>>, ?, ?> coder
                = Coder.mkRationalsCoder(
                field,
                Coder.mkMultivariateCoder(ring, allLetters));

        System.out.print("dimension of matrix? <row,col>: ");
        var buffer = s.nextLine();
        String[] splitBuffer = buffer.split(",");
        if (splitBuffer.length != 2) {
            System.out.println("wrong input");
            return;
        }
        clearConsole();
        row = Integer.parseInt(splitBuffer[0]);
        col = Integer.parseInt(splitBuffer[1]);
        matrix = new Rational[row][col];
        for (int i = 0; i < row; i++) {
            System.out.print("input for row " + (i+1) + " (split entries with <,>):");
            buffer = s.nextLine();
            splitBuffer = buffer.split(",");
            if (splitBuffer.length != col) {
                System.out.println("wrong input");
                return;
            }
            for (int j = 0; j < col; j++) {
                matrix[i][j] = coder.parse(splitBuffer[j]);
            }
        }
        printMatrix(matrix);
    }

    //bruh
    public static void clearConsole()
    {
        //a bunch of new line
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

    private static void printMatrix(Rational[][] matrix) {
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

    static String rationalToString(Rational<MultivariatePolynomial<BigInteger>> in) {
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
            else if (current == '+' || current == '-' || current == '/') {
                output.append(" " + current + " ");
            }
            else {
                output.append(current);
            }
        }
        return output.toString();
    }
}
