import cc.redberry.rings.Rational;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;

import java.util.Scanner;
import java.util.Stack;

public class Matrix {
    private int rows;
    private int cols;
    private Stack<Rational<MultivariatePolynomial<BigInteger>>[][]> matrixStates;
    private Stack<String> operations;
    private String name;
    private static int matrixCounter = 1;
    public Matrix (Rational<MultivariatePolynomial<BigInteger>>[][] squareArr) {
        this(squareArr, nextDefaultName());
        matrixCounter++;
    }

    public Matrix (Rational<MultivariatePolynomial<BigInteger>>[][] squareArr, String name) {
        matrixStates = new Stack<>();
        operations = new Stack<>();
        matrixStates.push(squareArr);
        operations.push("Initial matrix");
        this.rows = squareArr.length;
        this.cols = squareArr[0].length;
        this.name = name;
    }

    /**
     *
     * @return an entry from the most recent matrix state
     */
    public Rational<MultivariatePolynomial<BigInteger>> get(int row, int col) {
        return matrixStates.peek()[row][col];
    }

    /**
     *
     * @return makes a copy of the most recent matrix
     */
    public Rational<MultivariatePolynomial<BigInteger>>[][] getMatrix() {
        return copyMatrix(matrixStates.peek());
    }

    private Rational<MultivariatePolynomial<BigInteger>>[][] copyMatrix(Rational<MultivariatePolynomial<BigInteger>>[][] mat) {
        Rational<MultivariatePolynomial<BigInteger>>[][] copy = new Rational[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                copy[i][j] = Parser.parse(Printer.rationalToString(mat[i][j]));
            }
        }
        return copy;
    }

    public void add(Rational<MultivariatePolynomial<BigInteger>>[][] mat) {
        add(mat, "Unknown");
    }

    public void add(Rational<MultivariatePolynomial<BigInteger>>[][] mat, String op) {
        matrixStates.push(mat);
        operations.push(op);
    }

    /**
     *
     * @return the matrix removed
     */
    public Rational<MultivariatePolynomial<BigInteger>>[][] popMatrix() {
        if (matrixStates.size() > 1) {
            return matrixStates.pop();
        }
        return null;
    }

    public String popOperation() {
        if (operations.size() > 1) {
            return operations.pop();
        }
        return null;
    }

    public String undo() {
        popMatrix();
        return popOperation();
    }

    public static String nextDefaultName() {
        return "matrix" + matrixCounter;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public String preview() {
        return name + " (" + rows + "x" + cols + ")";
    }

    public String getName() {
        return this.name;
    }

    /**
     *
     * @param matrices
     * @param s
     * @param prompt
     * @return null if default name requested, otherwise return desired name
     */
    public static Matrix namePicker(MatrixCollection matrices, Scanner s, Rational<MultivariatePolynomial<BigInteger>>[][] squareArr, String prompt) {
        String name;
        while (true) {
            System.out.print(prompt);
            name = s.nextLine().strip();
            Printer.clearConsole();
            if (name.isEmpty()) {
                return new Matrix(squareArr);
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
        return new Matrix(squareArr, name);
    }

    public static Matrix namePicker(MatrixCollection matrices, Scanner s, Rational<MultivariatePolynomial<BigInteger>>[][] squareArr) {
        return namePicker(matrices, s, squareArr,"enter new matrix name: ");
    }

    private static boolean isName(String str) {
        return !Character.isDigit(str.charAt(0));
    }
}
