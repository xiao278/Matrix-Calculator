import cc.redberry.rings.Rational;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;

import java.util.Arrays;

public class MatrixFunctions {
    private static final Rational<MultivariatePolynomial<BigInteger>> zero = Parser.parse("0");

    public static Rational<MultivariatePolynomial<BigInteger>>[][] product(Matrix left, Matrix right) {
        if (left.cols != right.rows) return null;
        int inner = left.cols;
        Rational<MultivariatePolynomial<BigInteger>>[][] product = new Rational[left.rows][right.cols];
        for (int i = 0; i < product.length; i++) {
            for (int j = 0; j < product[i].length; j++) {
                Rational sum = Parser.parse("0");
                for (int k = 0; k < inner; k++) {
                    sum = sum.add(left.get(i, k).multiply(right.get(k, j)));
                }
                product[i][j] = sum;
            }
        }

        return product;
    }

    public static Rational<MultivariatePolynomial<BigInteger>>[][] transpose (Matrix m) {
        Rational<MultivariatePolynomial<BigInteger>>[][] mt = new Rational[m.cols][m.rows];
        for (int i = 0; i < m.cols; i++) {
            for (int j = 0; j < m.rows; j++) {
                mt[i][j] = m.get(j, i);
            }
        }
        return mt;
    }

    /**
     *
     * @param m
     * @return null if matrix is not square, else returns the determinant
     */
    public static Rational<MultivariatePolynomial<BigInteger>> findDeterminant (Matrix m) {
        if (m.rows != m.cols) return null;
        return findDeterminantRecursive(m.getMatrixCopy());
    }

    public static Rational<MultivariatePolynomial<BigInteger>> findDeterminantRecursive (Rational<MultivariatePolynomial<BigInteger>>[][] m) {
        if (m.length == 2) {
            return m[0][0].multiply(m[1][1]).subtract(m[0][1].multiply(m[1][0]));
        }

        Rational<MultivariatePolynomial<BigInteger>> zero = Parser.parse("0");
        Rational<MultivariatePolynomial<BigInteger>> sum = Parser.parse("0");

        for (int i = 0; i < m.length; i++) {
            Rational<MultivariatePolynomial<BigInteger>>[][] nextMatrix = new Rational[m.length - 1][m.length - 1];
            if (m[0][i].equals(zero)) continue;
            for (int copyRow = 0; copyRow < m.length - 1; copyRow++) {
                for (int copyCol = 0; copyCol < m.length - 1; copyCol++) {
                    int sourceRow = copyRow + 1;
                    int sourceCol = copyCol + ((copyCol >= i) ? 1 : 0);
                    nextMatrix[copyRow][copyCol] = m[sourceRow][sourceCol];
                }
            }
            if (i % 2 == 0) {
                sum = sum.add(findDeterminantRecursive(nextMatrix).multiply(m[0][i]));
            }
            else {
                sum = sum.subtract(findDeterminantRecursive(nextMatrix).multiply(m[0][i]));
            }
        }
        return sum;
    }


    public static Rational<MultivariatePolynomial<BigInteger>>[][] matrixAdd(Matrix left, Matrix right, int coefficient) {
        var result = left.getMatrixCopy();
        for (int i = 0; i < left.rows; i++) {
            for (int j = 0; j < left.cols; j++) {
                result[i][j] = result[i][j].add(right.get(i,j).multiply(Parser.parse("" + coefficient)));
            }
        }

        return result;
    }

    /**
     * Performs row reduction on a given matrix in n^3 time
     * @param matrix matrix to be row reduced. Will add changed matrix into the matrix stack
     * @return pivot column array, pivotCol[r] gives the col number for row r.
     */
    public static int[] rowReduce(Matrix matrix) {
        var A = matrix.getMatrixCopy();
        //pivotRow[i] is the row index of column i, -1 means not found or rows of zero, used for sorting
        int[] pivotRow = new int[matrix.cols];
        Arrays.fill(pivotRow, -1);
        //pivotCol[i] is the col index of row i, used to check if row already has a pivot
        int[] pivotCol = new int[matrix.rows];
        Arrays.fill(pivotCol, -1);
        for (int col = 0; col < matrix.cols; col++) {
            for (int row = 0; row < matrix.rows; row++) {
                if (!A[row][col].equals(zero) && pivotCol[row] == -1) {
                    pivotRow[col] = row;
                    pivotCol[row] = col;
                    var divisor = A[row][col];
                    for (int c = 0; c < matrix.cols; c++) {
                        A[row][c] = A[row][c].divide(divisor);
                    }
                    for (int r = 0; r < matrix.rows; r++) {
                        if (r != row) {
                            var ratio = A[r][col];
                            for (int c = 0; c < matrix.cols; c++) {
                                A[r][c] = A[r][c].subtract(A[row][c].multiply(ratio));
                            }
                        }
                    }
                    break;
                }
            }
        }

        int nextAvailableRow = 0;
        var B = new Rational[matrix.rows][matrix.cols];
        int rank = 0;
        int[] bPivotCol = new int[pivotCol.length];
        //flawed, sets row of zero when clearly no need
        for (int j : pivotRow) {
            if (j >= 0) {
                rank++;
                B[nextAvailableRow] = Arrays.copyOf(A[j], A[j].length);
                bPivotCol[nextAvailableRow] = pivotCol[j];
                nextAvailableRow++;
            }
        }

        bPivotCol = Arrays.copyOf(bPivotCol, rank);

        for (int i = A.length - 1; i >= nextAvailableRow; i--) {
            Arrays.fill(B[i], zero);
        }

        matrix.insert(B, "Row Reduction");
        return bPivotCol;
    }

    /**
     * for A mxn, b mx1 matrices, finds all x for which Ax=b
     * @param A an mxn matrix
     * @param b an m length array of Rational, represents mx1 transposed column vector
     * @return null if solution is inconsistent, otherwise returns an array of vectors. [0] is the offset, the rest is the span. if size is 1 then solution is unique
     */
    public static Rational<MultivariatePolynomial<BigInteger>>[][] solve(Matrix A, Rational<MultivariatePolynomial<BigInteger>>[] b) {
        var zero = Parser.parse("0");
        //an augmented matrix;
        Rational[][] temp = new Rational[A.rows][A.cols + 1];

        //copy all of A into the left part
        for (int r = 0; r < A.rows; r++) {
            for (int c = 0; c < A.cols; c++) {
                temp[r][c] = A.get(r,c);
            }
        }

        //copy b into rightmost column
        for (int r = 0; r < b.length; r++) {
            temp[r][A.cols] = b[r];
        }

        Matrix Ab = new Matrix(temp, "");
        int[] pivotCols = rowReduce(Ab);
        Rational[][] x = new Rational[A.cols][1];
        int nullity = Ab.cols - pivotCols.length;

        //row of zeroes with the augmented part nonzero. i.e. 0x=3 is never true
        if (pivotCols[pivotCols.length - 1] == Ab.cols - 1) return null;

        boolean[] isPivotCol = new boolean[Ab.cols];
        Arrays.fill(isPivotCol, false);
        for (int col: pivotCols) {
            isPivotCol[col] = true;
        }
        int[] nonPivotCols = new int[nullity];
        int nonPivotColTracker = 0;
        for (int i = 0; i < isPivotCol.length; i++) {
            if (!isPivotCol[i]) {
                nonPivotCols[nonPivotColTracker++] = i;
            }
        }

        Rational[][] solution = new Rational[nullity + 1][A.cols];

        for (int i = 0; i < solution.length; i++) {
            Arrays.fill(solution[i], zero);
        }

        for (int i = 0; i < A.rows; i++) {
            solution[0][i] = Ab.get(i, Ab.cols - 1).negate();
        }

        var one = Parser.parse("1");
        for (int i = 0; i < nonPivotCols.length; i++) {
            for (int j = 0; j < Ab.rows; j++) {
                solution[i + 1][j] = Ab.get(j, nonPivotCols[i]).negate();
            }
            solution[i + 1][nonPivotCols[i]] = one;
        }

        Printer.printMatrix(solution);

        return null;
    }
}
