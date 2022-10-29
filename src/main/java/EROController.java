import cc.redberry.rings.Rational;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;

import java.util.Scanner;

public class EROController {
    private static Rational<MultivariatePolynomial<BigInteger>>[][] matrix;
    private static Scanner s;

    public static void start(Rational<MultivariatePolynomial<BigInteger>>[][] in, Scanner scanner) {
        s = scanner;
        matrix = in;
        while (true) {
            Printer.clearConsole();
            Printer.printMatrix(matrix);
            System.out.println("Enter a command (type \"h\" for more info): ");
            System.out.print(">");
            if (processInput()) return;
        }
    };

    private static void moreInfo() {
        Printer.clearConsole();
        System.out.println("Type \"q\" to return to main menu\n");
        System.out.println("Performing elementary row operations formats: ");
        System.out.println("    1) Scaling: \"Rx => cRx\" means scale row x by c");
        System.out.println("    2) Adding: \"Rx => Rx + cRy\" means add (c times row y) to row x");
        System.out.println("    3) Swapping: \"Rx <> Ry\" means swap row x with row y\n");
        System.out.println("---Press enter to go back---");
        s.nextLine();
    }

    private static boolean processInput() {
        String str = s.nextLine();
        str = str.strip();
        switch (str) {
            case "h" -> {
                moreInfo();
            }
            case "q" -> {
                Printer.clearConsole();
                return true;
            }
            default -> {

            }
        }
        return false;
    }
}
