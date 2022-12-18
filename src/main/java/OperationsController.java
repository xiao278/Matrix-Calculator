import cc.redberry.rings.Rational;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;
import java.util.Scanner;

public class OperationsController {
    private static Scanner s;

    public static void start(Matrix m, Scanner scanner) {
        s = scanner;
        while (true) {
            System.out.println(m);
            System.out.println("Enter a command (type \"h\" for more info and \"q\" to go back): ");
            System.out.print(">");
            if (processInput(m)) return;
        }
    }

    private static void moreInfo() {
        Printer.clearConsole();
        System.out.println("\ni) Some Examples for Performing elementary row operations - use \"R\" to denote row (i.e \"R3\" is row3):");
        System.out.println("    - Scaling: \"R3 *= 8\" means \"scale row3 by 8\"");
        System.out.println("    - Dividing: \"R2 /= a+b\" means \"scale row2 by (1/a+b)\"");
        System.out.println("    - Adding: \"R1 += 5aR4\" means \"add (5a times row4) to row1\"");
        System.out.println("    - Subtracting: \"R3 -= 4R1\" means \"subtract (4 times row1) from row3\"");
        System.out.println("    - Swapping: \"R6 <> R3\" means \"swap row6 with row3\"");

        System.out.println("\nii) performing elementary column operations: replace 'R' with 'C'");
        System.out.println("    - Example: \"C5 += 7C1\" means \"add (7 times column1) to column5");

        System.out.println("\n---Press enter to go back---");
        s.nextLine();
        Printer.clearConsole();
    }

    private static boolean processInput(Matrix m) {
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
                System.out.println("undone operation: " + m.undo());
            }
            default -> {
                try {
                    Printer.clearConsole();
                    parseOperation(m, str);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
        return false;
    }

    private static void parseOperation(Matrix m, String input) throws Exception {
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

        var matrix = m.getMatrixCopy();
        String operation;

        if (destination < 0 || destination >= m.rows) throw new Exception("invalid destination " + RC);
        String operandStr = split[1].strip();

        if (op.equals("*") || op.equals("/")) {
            if (operandStr.indexOf(RC) != -1) throw new Exception("Scaling can only be done with scalars");
            Rational<MultivariatePolynomial<BigInteger>> scale = Parser.parse(operandStr);
            if (op.equals("/")) scale = scale.pow(-1);
            if (RC == 'R') {
                for (int i = 0; i < m.cols; i++) {
                    matrix[destination][i] = matrix[destination][i].multiply(scale);
                }
            }
            else {
                for (int i = 0; i < m.rows; i++) {
                    matrix[i][destination] = matrix[i][destination].multiply(scale);
                }
            }
            operation = RC + "" + (destination + 1) + " -> " + "(" + Matrix.rationalToString(scale) + ")" + RC + (destination + 1);
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
            String scaleString = Matrix.rationalToString(scale);
            if (op.equals("-")) {
                scale = scale.multiply(Parser.parse("-1"));
            }
            if (RC == 'R') {
                for (int i = 0; i < m.cols; i++) {
                    matrix[destination][i] = matrix[destination][i].add(matrix[target][i].multiply(scale));
                }
            }
            else {
                for (int i = 0; i < m.rows; i++) {
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
                for (int i = 0; i < m.rows; i++) {
                    var temp = matrix[i][destination];
                    matrix[i][destination] = matrix[i][target];
                    matrix[i][target] = temp;
                }
            }
            operation = RC + "" + (destination + 1) + " <-> " + RC + (target + 1);
        }

        else throw new Exception("invalid operation");

        m.insert(matrix, operation);
    }
}
