import cc.redberry.rings.Rational;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;
import java.util.Scanner;

public class OperationsController {
    private static Matrix matrixSystem;
    private static Scanner s;

    public static void start(Matrix m, Scanner scanner) {
        s = scanner;
        matrixSystem = m;
        while (true) {
            Printer.printMatrix(matrixSystem.getMatrix());
            System.out.println("Enter a command (type \"h\" for more info): ");
            System.out.print(">");
            if (processInput()) return;
        }
    }

    private static void moreInfo() {
        Printer.clearConsole();
        System.out.println("i) Type \"q\" to return to main menu");

        System.out.println("\nii) Performing elementary row operations - use \"Rx\" to denote row x (i.e R3 is row 3):");
        System.out.println("    - Scaling: \"Rx *= c\" means \"scale row x by c\"");
        System.out.println("    - Dividing: \"Rx /= c\" means \"scale row x by (1/c)\"");
        System.out.println("    - Adding: \"Rx += cRy\" means \"add (c times row y) to row x\"");
        System.out.println("    - Subtracting: \"Rx -= cRy\" means \"subtract (c times row y) from row x\"");
        System.out.println("    - Swapping: \"Rx <> Ry\" means \"swap row x with row y\"");

        System.out.println("\niii) performing elementary column operations: replace 'R' with 'C'");

        System.out.println("\n---Press enter to go back---");
        s.nextLine();
        Printer.clearConsole();
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
            case "u" -> {
                Printer.clearConsole();
                System.out.println("undone operation: " + matrixSystem.undo());
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
        String destinationStr = split[0].strip();
        if (destinationStr.length() <= 1) throw new Exception("Maformed destination row/col");

        char RC = destinationStr.charAt(0);
        if (RC != 'R' && RC != 'C') throw new Exception("error formatting");
        int destination = Integer.parseInt(destinationStr.substring(1)) - 1;

        var matrix = matrixSystem.getMatrix();
        String operation;

        if (destination < 0 || destination >= matrixSystem.getRows()) throw new Exception("invalid destination " + RC);
        String operandStr = split[1].strip();

        if (op.equals("*") || op.equals("/")) {
            if (operandStr.indexOf(RC) != -1) throw new Exception("Scaling can only be done with scalars");
            Rational<MultivariatePolynomial<BigInteger>> scale = Parser.parse(operandStr);
            if (op.equals("/")) scale = scale.pow(-1);
            if (RC == 'R') {
                for (int i = 0; i < matrixSystem.getCols(); i++) {
                    matrix[destination][i] = matrix[destination][i].multiply(scale);
                }
            }
            else {
                for (int i = 0; i < matrixSystem.getRows(); i++) {
                    matrix[i][destination] = matrix[i][destination].multiply(scale);
                }
            }
            operation = RC + "" + (destination + 1) + " -> " + "(" + Printer.rationalToString(scale) + ")" + RC + (destination + 1);
        }

        else if (op.equals("+") || op.equals("-")) {
            split = operandStr.strip().split(RC + "");
            if (split.length != 2) throw new Exception("Cannot add scalar");
            Rational<MultivariatePolynomial<BigInteger>> scale;
            int target;
            if (split[0].isEmpty()) {
                scale = Parser.parse("1");
            }
            else {
                scale = Parser.parse(split[0]);
            }
            target = Integer.parseInt(split[1]) - 1;
            if (target < 0 || target >= matrix.length) throw new Exception("invalid target " + RC);
            String scaleString = Printer.rationalToString(scale);
            if (op.equals("-")) {
                scale = scale.multiply(Parser.parse("-1"));
            }
            if (RC == 'R') {
                for (int i = 0; i < matrixSystem.getCols(); i++) {
                    matrix[destination][i] = matrix[destination][i].add(matrix[target][i].multiply(scale));
                }
            }
            else {
                for (int i = 0; i < matrixSystem.getRows(); i++) {
                    matrix[i][destination] = matrix[i][destination].add(matrix[i][target].multiply(scale));
                }
            }
            operation = RC + "" +(destination + 1) + " -> " + RC + (destination + 1) + (op.equals("-") ? " - " : " + ")
                    + "(" + scaleString + ")" + RC + (target + 1);
        }

        else if (op.equals("swap")) {
            split = operandStr.strip().split(RC + "");
            if (!split[0].isEmpty()) throw new Exception("Cannot scale during swap");
            int target = Integer.parseInt(split[1]) - 1;
            if (target < 0 || target >= matrix.length) throw new Exception("invalid target row");
            if (RC == 'R') {
                var temp = matrix[destination];
                matrix[destination] = matrix[target];
                matrix[target] = temp;
            }
            else {
                for (int i = 0; i < matrixSystem.getRows(); i++) {
                    var temp = matrix[i][destination];
                    matrix[i][destination] = matrix[i][target];
                    matrix[i][target] = temp;
                }
            }
            operation = RC + "" + (destination + 1) + " <-> " + RC + (target + 1);
        }

        else throw new Exception("invalid operation");

        matrixSystem.add(matrix, operation);
    }
}
