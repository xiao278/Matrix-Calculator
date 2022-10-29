import cc.redberry.rings.Rational;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.io.Coder;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;

import java.util.Scanner;

public class EROController {
    private static Rational<MultivariatePolynomial<BigInteger>>[][] matrix;
    private static Scanner s;
    private static Coder coder;

    public static void start(Rational<MultivariatePolynomial<BigInteger>>[][] in, Scanner scanner, Coder c) {
        s = scanner;
        coder = c;
        matrix = in;
        while (true) {
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
        System.out.println("    1) Scaling: \"Rx *= c\" means scale row x by c");
        System.out.println("    2) Adding: \"Rx += cRy\" means add (c times row y) to row x");
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
                try {
                    parseOperation(str);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
        return false;
    }

    private static void parseOperation(String input) throws Exception {
        int scaleIndex = input.indexOf("*=");
        int addIndex = input.indexOf("+=");
        int subIndex = input.indexOf("-=");
        int swapIndex = input.indexOf("<>");

        String[] split;
        String op = "none";

        int total = scaleIndex + addIndex + subIndex + swapIndex;
        if (total != -4) {
            if (scaleIndex == total + 3) {
                split = input.split("\\*=");
                op = "*";
            }
            else if (addIndex == total + 3) {
                split = input.split("\\+=");
                op = "+";
            }
            else if (subIndex == total + 3) {
                split = input.split("-=");
                op = "-";
            }
            else if (swapIndex == total + 3) {
                split = input.split("<>");
                op = "swap";
            }
            else {
                throw new Exception("No operator found");
            }
        }
        else {
            throw new Exception("Bad formatting");
        }
        if (split.length != 2) throw new Exception("Bad formatting");
        String destinationRowStr = split[0].strip();
        System.out.println(destinationRowStr);
        if ((destinationRowStr.charAt(0) != 'R' && destinationRowStr.charAt(0) != 'r') || destinationRowStr.length() <= 1) throw new Exception("Malformed destination row");
        int destinationRow = Integer.parseInt(destinationRowStr.substring(1)) - 1;
        if (destinationRow < 0 || destinationRow >= matrix.length) throw new Exception("invalid destination row");

        String operandStr = split[1].strip();
        if (op.equals("*")) {
            Rational<MultivariatePolynomial<BigInteger>> scale = (Rational<MultivariatePolynomial<BigInteger>>) coder.parse(operandStr);
            for (int i = 0; i < matrix[destinationRow].length; i++) {
                matrix[destinationRow][i] = matrix[destinationRow][i].multiply(scale);
            }
            return;
        }

        if (op.equals("+") || op.equals("-")) {
            split = operandStr.split("[r,R]");
            Rational<MultivariatePolynomial<BigInteger>> scale;
            if (split.length == 1) {
                if (operandStr.indexOf('r') == operandStr.length() - 1 || operandStr.indexOf('R') == operandStr.length() - 1)
                    throw new Exception("Invalid target row");
                scale = (Rational<MultivariatePolynomial<BigInteger>>) coder.parse("1");
            }
            else if (split.length == 2) {
                scale = (Rational<MultivariatePolynomial<BigInteger>>) coder.parse(split[0]);
            }
            else throw new Exception("Invalid target row");
            if (op.equals("-")) scale = scale.multiply((Rational<MultivariatePolynomial<BigInteger>>) coder.parse("-1"));
        }
    }
}
