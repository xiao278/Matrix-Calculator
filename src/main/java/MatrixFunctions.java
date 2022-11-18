import cc.redberry.rings.Rational;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;

public class MatrixFunctions {

    public static Rational<MultivariatePolynomial<BigInteger>>[][] product(Matrix left, Matrix right) {
        if (left.getCols() != right.getRows()) return null;
        int inner = left.getCols();
        Rational<MultivariatePolynomial<BigInteger>>[][] product = new Rational[left.getRows()][right.getCols()];
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
        Rational<MultivariatePolynomial<BigInteger>>[][] mt = new Rational[m.getCols()][m.getRows()];
        for (int i = 0; i < m.getCols(); i++) {
            for (int j = 0; j < m.getRows(); j++) {
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
        if (m.getRows() != m.getCols()) return null;
        return findDeterminantRecursive(m.getMatrix());
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
}