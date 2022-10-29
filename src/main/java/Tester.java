import cc.redberry.rings.*;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.io.Coder;
import cc.redberry.rings.poly.*;
import cc.redberry.rings.poly.univar.*;
import cc.redberry.rings.poly.multivar.*;
import cc.redberry.rings.primes.SmallPrimes;

import java.util.ArrayList;
import java.util.Arrays;

import static cc.redberry.rings.poly.PolynomialMethods.*;
import static cc.redberry.rings.Rings.*;

public class Tester {
    public static void main(String[] args) {
//        IntegersZp64 zpRing = Zp64(SmallPrimes.nextPrime(1 << 15));
//
//        //findng GCD example
//        var bruh = MultivariatePolynomialZp64.parse("x^2 + 2*x*y + y^2", zpRing);
//        var bruh2 = MultivariatePolynomialZp64.parse("y*x + y^2", zpRing);
//        var result = MultivariateGCD.BrownGCD(bruh, bruh2);
//        System.out.println(result);
//
//        //finding coefficient example
//        var factorTest = MultivariatePolynomialZp64.parse("8*x + 12*y + 4000*z", zpRing);
//        var coefs = factorTest.coefficients();
//        System.out.println(Arrays.toString(coefs));


        //rational test
        String[] allLetters = new String[26];
        for (int i = 0; i < 26; i++) {
            allLetters[i] = "" + (char)('a' + i);
        }
        MultivariateRing<MultivariatePolynomial<BigInteger>> ring = MultivariateRing(26, Z);
        Rationals<MultivariatePolynomial<BigInteger>> field = Frac(ring);
        Coder<Rational<MultivariatePolynomial<BigInteger>>, ?, ?> coder
                = Coder.mkRationalsCoder(
                field,
                Coder.mkMultivariateCoder(ring, allLetters));
        Rational<MultivariatePolynomial<BigInteger>> a = coder.parse("(x+y)^3");
        System.out.println(rationalToString(a));

    }
          static String rationalToString(Rational<MultivariatePolynomial<BigInteger>> in) {
                StringBuilder output = new StringBuilder();
                String temp = in.toString();
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
                    else if (current == '+' || current == '-' || current == '/') {
                        output.append(" " + current + " ");
                    }
                    else {
                        output.append(current);
                    }
                }
                return output.toString();
        }
}
