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

public class Initializer {
    public static Rational<MultivariatePolynomial<BigInteger>>[][] start(Scanner s) throws Exception {
        //[row][col]
        Rational<MultivariatePolynomial<BigInteger>>[][] matrix;
        int row;
        int col;
        Printer.clearConsole();
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

        System.out.print("dimension of matrix? \"row,col\": ");
        var buffer = s.nextLine();
        String[] splitBuffer = buffer.split(",");
        if (splitBuffer.length != 2) {
            throw new Exception("bad input");
        }
        Printer.clearConsole();
        row = Integer.parseInt(splitBuffer[0]);
        col = Integer.parseInt(splitBuffer[1]);
        matrix = new Rational[row][col];
        System.out.println("input the rows (split entries with \",\"):");
        for (int i = 0; i < row; i++) {
            System.out.print("row " + (i+1) + ": ");
            buffer = s.nextLine();
            splitBuffer = buffer.split(",");
            if (splitBuffer.length != col) {
                throw new Exception("bad input");
            }
            for (int j = 0; j < col; j++) {
                matrix[i][j] = coder.parse(splitBuffer[j]);
            }
        }
        return matrix;
    }
}
