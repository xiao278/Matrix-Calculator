import cc.redberry.rings.Rational;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.io.Coder;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;
import java.util.Scanner;

public class Initializer {
    public static Rational<MultivariatePolynomial<BigInteger>>[][] start(Scanner s, Coder coder) throws Exception {
        //[row][col]
        Rational<MultivariatePolynomial<BigInteger>>[][] matrix;
        int row;
        int col;
        Printer.clearConsole();

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
                matrix[i][j] = (Rational<MultivariatePolynomial<BigInteger>>) coder.parse(Parser.parseInput(splitBuffer[j]));
            }
        }
        return matrix;
    }
}
