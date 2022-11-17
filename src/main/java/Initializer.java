import cc.redberry.rings.Rational;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;
import java.util.Scanner;

public class Initializer {
    public static void start(MatrixCollection matrices, Scanner s) throws Exception {
        //[row][col]
        Rational<MultivariatePolynomial<BigInteger>>[][] matrix;
        int row;
        int col;

        System.out.print("dimension of matrix? <row>x<col>: ");
        var buffer = s.nextLine();
        String[] splitBuffer = buffer.split("x");
        if (splitBuffer.length != 2) {
            throw new Exception("invalid formatting");
        }
        Printer.clearConsole();
        row = Integer.parseInt(splitBuffer[0]);
        col = Integer.parseInt(splitBuffer[1]);
        matrix = new Rational[row][col];
        System.out.println("input the rows, " + col + " entries each (separate entries with \",\"):");
        for (int i = 0; i < row; i++) {
            System.out.print("row " + (i+1) + ": ");
            buffer = s.nextLine();
            splitBuffer = buffer.split(",");
            if (splitBuffer.length != col) {
                throw new Exception("expected " + col + " entries");
            }
            for (int j = 0; j < col; j++) {
                matrix[i][j] = Parser.parse(splitBuffer[j]);
            }
        }
        //insert into collections
        var temp = Controller.namePicker(matrix);
        matrices.add(temp);
    }
}
