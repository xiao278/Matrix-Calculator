import cc.redberry.rings.Rational;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.io.Coder;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;
import java.util.Scanner;

public class Initializer {
    public static void start(MatrixCollection matrices, Scanner s) throws Exception {
        //[row][col]
        Rational<MultivariatePolynomial<BigInteger>>[][] matrix;
        int row;
        int col;
        String name;

        while (true) {
            System.out.print("enter a name for matrix: ");
            name = s.nextLine().strip();
            Printer.clearConsole();
            if (name.isEmpty()) {
                name = Matrix.nextDefaultName();
                break;
            }
            if (isName(name)) {
                if (matrices.contains(name)) System.out.println("Error: duplicate naming");
                else {
                    break;
                }
            }
            else {
                System.out.println("Error: invalid name, first character cannot be a number");
            }
        }

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
                matrix[i][j] = Parser.parse(splitBuffer[j]);
            }
        }
        //insert into collections
        var temp = new Matrix(matrix, name);
        matrices.add(temp);
    }

    private static boolean isName(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }
}
