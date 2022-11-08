import cc.redberry.rings.Rational;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;

public class Multiplication {

    public static Rational<MultivariatePolynomial<BigInteger>>[][] product(Matrix A, Matrix B) {
        if (A.getCols() != B.getRows()) return null;
        int inner = A.getCols();
        Rational<MultivariatePolynomial<BigInteger>>[][] product = new Rational[A.getCols()][B.getCols()];
        for (int i = 0; i < product.length; i++) {
            for (int j = 0; j < product[i].length; j++) {
                Rational sum = Parser.parse("0");
                for (int k = 0; k < inner; k++) {
                    sum = sum.add(A.get(i, k).multiply(B.get(k, j)));
                }
                product[i][j] = sum;
            }
        }

        return product;
    }
}
