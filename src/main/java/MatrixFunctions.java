import cc.redberry.rings.Rational;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;

import java.util.Arrays;

public class MatrixFunctions {

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
     * @return the rank of the matrix
     */
    public static int rowReduce(Matrix matrix) {
        var zero = Parser.parse("0");
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
        //flawed, sets row of zero when clearly no need
        for (int j : pivotRow) {
            if (j >= 0) {
                rank++;
                B[nextAvailableRow++] = Arrays.copyOf(A[j], A[j].length);
            }
        }

        for (int i = A.length - 1; i >= nextAvailableRow; i--) {
            Arrays.fill(B[i], zero);
        }

        matrix.insert(B, "Row Reduction");
        return rank;
    }

    /**
     * for A mxn, b mx1 matrices, finds all x for which Ax=b
     * @param A an mxn matrix
     * @param b an mx1 matrix
     * @return an array of nx1 matrices
     */
    public static Rational<MultivariatePolynomial<BigInteger>>[][][] solve(Matrix A, Matrix b) {
        //an augmented matrix;
        Rational[][] Ab = new Rational[A.rows][A.cols + b.cols];

        //copy all of A into the left part
        for (int r = 0; r < A.rows; r++) {
            for (int c = 0; c < A.cols; c++) {
                Ab[r][c] = A.get(r,c);
            }
        }

        //copy b into rightmost column
        for (int r = 0; r < b.rows; r++) {
            Ab[r][A.cols] = b.get(r,0);
        }

        Matrix temp = new Matrix(Ab, "");
        rowReduce(temp);

        return null;
    }
}
