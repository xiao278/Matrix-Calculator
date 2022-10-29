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
    }

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
                    Printer.clearConsole();
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
        int divIndex = input.indexOf("/=");
        int addIndex = input.indexOf("+=");
        int subIndex = input.indexOf("-=");
        int swapIndex = input.indexOf("<>");

        int numOps = 5;

        String[] split;
        String op = "none";

        int total = scaleIndex + addIndex + subIndex + swapIndex + divIndex;
        if (total != -1*numOps) {
            if (scaleIndex == total + numOps - 1) {
                split = input.split("\\*=");
                op = "*";
            }
            else if (divIndex == total + numOps - 1) {
                split = input.split("/=");
                op = "/";
            }
            else if (addIndex == total + numOps - 1) {
                split = input.split("\\+=");
                op = "+";
            }
            else if (subIndex == total + numOps - 1) {
                split = input.split("-=");
                op = "-";
            }
            else if (swapIndex == total + numOps - 1) {
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
        if ((destinationRowStr.charAt(0) != 'R' && destinationRowStr.charAt(0) != 'r') || destinationRowStr.length() <= 1) throw new Exception("Malformed destination row");
        int destinationRow = Integer.parseInt(destinationRowStr.substring(1)) - 1;
        if (destinationRow < 0 || destinationRow >= matrix.length) throw new Exception("invalid destination row");

        String operandStr = split[1].strip();
        if (op.equals("*") || op.equals("/")) {
            if (operandStr.indexOf('r') != -1 || operandStr.indexOf('R') != -1) throw new Exception("Scaling can only be done with scalars");
            Rational<MultivariatePolynomial<BigInteger>> scale = (Rational<MultivariatePolynomial<BigInteger>>) coder.parse(operandStr);
            if (op.equals("/")) scale = scale.pow(-1);
            for (int i = 0; i < matrix[destinationRow].length; i++) {
                matrix[destinationRow][i] = matrix[destinationRow][i].multiply(scale);
            }
            return;
        }

        if (op.equals("+") || op.equals("-")) {
            split = operandStr.strip().split("[r,R]");
            Rational<MultivariatePolynomial<BigInteger>> scale;
            int targetRow;
            if (split[0].isEmpty()) {
                scale = (Rational<MultivariatePolynomial<BigInteger>>) coder.parse("1");
            }
            else {
                scale = (Rational<MultivariatePolynomial<BigInteger>>) coder.parse(split[0]);
            }
            targetRow = Integer.parseInt(split[1]) - 1;
            if (targetRow < 0 || targetRow >= matrix.length) throw new Exception("invalid target row");
            if (op.equals("-")) scale = scale.multiply((Rational<MultivariatePolynomial<BigInteger>>) coder.parse("-1"));
            for (int i = 0; i < matrix[destinationRow].length; i++) {
                matrix[destinationRow][i] = matrix[destinationRow][i].add(matrix[targetRow][i].multiply(scale));
            }
            return;
        }

        if (op.equals("swap")) {
            split = operandStr.strip().split("[r,R]");
            if (!split[0].isEmpty()) throw new Exception("Cannot scale during swap");
            int targetRow = Integer.parseInt(split[1]) - 1;
            if (targetRow < 0 || targetRow >= matrix.length) throw new Exception("invalid target row");
            var temp = matrix[destinationRow];
            matrix[destinationRow] = matrix[targetRow];
            matrix[targetRow] = temp;
            return;
        }
    }
}
