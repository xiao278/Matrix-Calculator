import cc.redberry.rings.Rational;
import cc.redberry.rings.Rationals;
import cc.redberry.rings.Rings;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.io.Coder;
import cc.redberry.rings.poly.MultivariateRing;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;

import static cc.redberry.rings.Rings.Frac;
import static cc.redberry.rings.Rings.Z;

//
public class Parser {
    private static Coder<Rational<MultivariatePolynomial<BigInteger>>, ?, ?> coder;
    private static String parseInput(String in) {
        if (in.length() <= 1) {
            return in;
        }
        StringBuilder out = new StringBuilder();
        out.append(in.charAt(0));
        for (int i = 1; i < in.length(); i++) {
            char prevChar = in.charAt(i-1);
            char curChar = in.charAt(i);
            if ((Character.isLetter(prevChar) && Character.isLetter(curChar)) ||
                    (Character.isDigit(prevChar) && Character.isLetter(curChar)) ||
                    (Character.isLetter(prevChar) && Character.isDigit(curChar))
            ) {
                out.append("*");
            }
            out.append(curChar);
        }
        return out.toString();
    }

    public static Rational<MultivariatePolynomial<BigInteger>> parse(String in) {
        if (coder == null) return null;
        return coder.parse(parseInput(in));
    }

    public static void initialize() {
        String[] allLetters = new String[26];
        for (int i = 0; i < 26; i++) {
            allLetters[i] = "" + (char)('a' + i);
        }
        MultivariateRing<MultivariatePolynomial<BigInteger>> ring = Rings.MultivariateRing(26, Z);
        Rationals<MultivariatePolynomial<BigInteger>> field = Frac(ring);
        coder = Coder.mkRationalsCoder(
                field,
                Coder.mkMultivariateCoder(ring, allLetters));
    }
}
