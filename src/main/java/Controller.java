import cc.redberry.rings.Rational;
import cc.redberry.rings.Rationals;
import cc.redberry.rings.Rings;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.io.Coder;
import cc.redberry.rings.poly.MultivariateRing;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;

import javax.swing.text.Position;
import java.util.Arrays;
import java.util.Scanner;

import static cc.redberry.rings.Rings.Frac;
import static cc.redberry.rings.Rings.Z;

public class Controller {
    private static Scanner s;
    private static Rational<MultivariatePolynomial<BigInteger>>[][] matrix;
    private static Coder<Rational<MultivariatePolynomial<BigInteger>>, ?, ?> coder;

    private static final String
            rowOps = "Perform ERO",
            transpose = "Transpose",
            guide = "User guide",
            exit = "Quit";

    private static final String[] options = new String[]{
            rowOps, transpose, guide, exit
    };

    public static void main(String[] args) {
        s = new Scanner(System.in);

        String[] allLetters = new String[26];
        for (int i = 0; i < 26; i++) {
            allLetters[i] = "" + (char)('a' + i);
        }
        MultivariateRing<MultivariatePolynomial<BigInteger>> ring = Rings.MultivariateRing(26, Z);
        Rationals<MultivariatePolynomial<BigInteger>> field = Frac(ring);
        coder = Coder.mkRationalsCoder(
                field,
                Coder.mkMultivariateCoder(ring, allLetters));

        try {
            matrix = Initializer.start(s, coder);
            Printer.clearConsole();
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }
        while (true) {
            printModes();
            processModes();
        }
    }

    private static void printModes() {
        for (int i = 0; i < options.length; i++) {
            System.out.println((i+1) + ") " + options[i]);
        }
        System.out.println("Choose one of the options (enter a number): ");
    }

    private static void processModes() {
        String str = s.nextLine();
        int choice;
        Printer.clearConsole();
        try {
            choice = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            System.out.println("Error: not a number, please try again");
            return;
        }

        if (choice < 1 || choice > options.length) {
            System.out.println("Error: invalid choice, please try again");
            return;
        }

        switch (options[choice - 1]) {
            case exit -> {
                System.exit(1);
            }
            case rowOps -> {
                EROController.start(matrix, s, coder);
            }
            case guide -> {
                System.out.println("wip");
            }
            case transpose -> {
                System.out.println("wip");
            }
        }
    }
}
