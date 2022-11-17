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
}
