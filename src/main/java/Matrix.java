import cc.redberry.rings.Rational;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;

import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Pattern;

public class Matrix {
    public final int rows;
    public final int cols;
    private Stack<Rational<MultivariatePolynomial<BigInteger>>[][]> matrixStates;
    private Stack<String> operations;
    private String name;
    private static int matrixCounter = 1;
    private final int matrixPadding = 0;

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

    public static String rationalToString(Rational<MultivariatePolynomial<BigInteger>> mat) {
        StringBuilder output = new StringBuilder();
        String temp = mat.toString();
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
                output.append(" ").append(current).append(" ");
            }
            else {
                output.append(current);
            }
        }
        return output.toString().strip();
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

    public Rational<MultivariatePolynomial<BigInteger>>[][] getMatrix() {
        return matrixStates.peek();
    }

    private Rational<MultivariatePolynomial<BigInteger>>[][] copyMatrix(Rational<MultivariatePolynomial<BigInteger>>[][] mat) {
        Rational<MultivariatePolynomial<BigInteger>>[][] copy = new Rational[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                copy[i][j] = Parser.parse(rationalToString(mat[i][j]));
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

//    public boolean hasVariables() {
//        return Pattern.compile("[a-z]+").matcher().find();
//        return null;
//    }

    public String toString(int index) {
        var matrix = matrixStates.get(index);
        String[][] toStringMatrix = new String[matrix.length][matrix[0].length];

        //max length for each column
        int[] columnLength = new int[matrix[0].length];
        Arrays.fill(columnLength, 0);

        //insert into toStringMatrix
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                toStringMatrix[i][j] = rationalToString(matrix[i][j]);
                columnLength[j] = Math.max(columnLength[j], toStringMatrix[i][j].length() + matrixPadding);
            }
        }

        //formatted print
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < matrix.length; i++) {
            out.append("[ ");
            //paddiing
            out.append(" ".repeat(matrixPadding));
            for (int j = 0; j < matrix[i].length; j++) {
                out.append(toStringMatrix[i][j]);
                int spaceNeeded = columnLength[j] - toStringMatrix[i][j].length();
                out.append(" ".repeat(Math.max(0, spaceNeeded)));
                if (j < matrix[i].length - 1) out.append(" | ");
            }
            out.append(" ]\n");
        }
        return out.toString();
    }

    public String toString() {
        return toString(matrixStates.size() - 1);
    }
}
