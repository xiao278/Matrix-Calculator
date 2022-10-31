import cc.redberry.rings.Rational;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;

import java.util.Arrays;
import java.util.Stack;

public class Matrix {
    private int rows;
    private int cols;
    private Stack<Rational<MultivariatePolynomial<BigInteger>>[][]> matrixStates;
    private String name;
    private static int matrixCounter;
    public Matrix (Rational<MultivariatePolynomial<BigInteger>>[][] squareArr) {
        this(squareArr, "matrix" + (matrixCounter++));
    }

    public Matrix (Rational<MultivariatePolynomial<BigInteger>>[][] squareArr, String name) {
        matrixStates = new Stack<>();
        matrixStates.push(squareArr);
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
        Rational<MultivariatePolynomial<BigInteger>>[][] copy = new Rational[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                copy[i][j] = Parser.parse(Printer.rationalToString(get(i,j)));
            }
        }
        return copy;
    }

    public void add(Rational<MultivariatePolynomial<BigInteger>>[][] mat) {
        matrixStates.push(mat);
    }

    /**
     *
     * @return the matrix removed
     */
    public Rational<MultivariatePolynomial<BigInteger>>[][] pop() {
        if (matrixStates.size() > 1) return matrixStates.pop();
        return null;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}
