import cc.redberry.rings.Rational;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;

import java.util.Stack;
import java.util.regex.Pattern;

public class Matrix {
    public final int rows;
    public final int cols;
    private Stack<Rational<MultivariatePolynomial<BigInteger>>[][]> matrixStates;
    private Stack<String> operations;
    private String name;
    private static int matrixCounter = 1;

    /**
     * creates a matrix with a default name
     * @param squareArr any double-nested array
     */
    public Matrix (Rational<MultivariatePolynomial<BigInteger>>[][] squareArr) {
        this(squareArr, nextDefaultName());
        matrixCounter++;
    }

    /**
     * creates a matrix with custom name
     * @param squareArr any double-nested array
     * @param name name for matrix
     */
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
    public Rational<MultivariatePolynomial<BigInteger>>[][] getMatrixCopy() {
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

    public void insert(Rational<MultivariatePolynomial<BigInteger>>[][] mat) {
        insert(mat, "Unknown");
    }

    public void insert(Rational<MultivariatePolynomial<BigInteger>>[][] mat, String op) {
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

    public String preview() {
        return name + " (" + rows + "x" + cols + ")";
    }

    public String getName() {
        return this.name;
    }

    public static boolean isName(String str) {
        return !str.equals("quit") && Pattern.matches("[0-9]*[^0-9]+.*", str);
    }

    public Stack<Rational<MultivariatePolynomial<BigInteger>>[][]> getMatrixStates() {
        return matrixStates;
    }

    public Stack<String> getOperations() {
        return operations;
    }
}
