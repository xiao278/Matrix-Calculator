import cc.redberry.rings.Rational;
import cc.redberry.rings.Rationals;
import cc.redberry.rings.Rings;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.io.Coder;
import cc.redberry.rings.poly.MultivariateRing;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;

import static cc.redberry.rings.Rings.*;

//
public class Parser {
    public static final MultivariateRing<MultivariatePolynomial<BigInteger>> ring = Rings.MultivariateRing(27, Z);
    public static String[] parserVariables = new String[]{"a","b","c","d","e","f","g",
            "h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","ß"};
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
            if ((isVariable(prevChar) && isVariable(curChar)) ||
                    (Character.isDigit(prevChar) && isVariable(curChar)) ||
                    (isVariable(prevChar) && Character.isDigit(curChar))
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
        Rationals<MultivariatePolynomial<BigInteger>> field = Frac(ring);
        coder = Coder.mkRationalsCoder(
                field,
                Coder.mkMultivariateCoder(ring, parserVariables));
    }

    public static boolean isVariable(char in) {
        return Character.isLetter(in) || in == 'ß';
    }
}
