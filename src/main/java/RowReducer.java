import java.util.Arrays;

public class RowReducer {

    /**
     * Performs ERO on a given matrix
     * @param matrix
     */
    public static void run(Matrix matrix) {
        var zero = Parser.parse("0");
        var A = matrix.getMatrixCopy();
        //pivotRow[i] is the row index of column i, -1 means not found, used for sorting
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
        //TODO: sort result matrix so that it becomes upper triangular matrix, using pivotRow. treat -1 as bigger than any other number
        matrix.add(A, "Row Reduction");
    }
}
