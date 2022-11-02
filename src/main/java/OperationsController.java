import cc.redberry.rings.Rational;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.io.Coder;
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

        System.out.println("\nii) Performing elementary row operations - use r or R to denote the row:");
        System.out.println("    • Scaling: \"Rx *= c\" means \"scale row x by c\"");
        System.out.println("    • Dividing: \"Rx /= c\" means \"scale row x by (1/c)\"");
        System.out.println("    • Adding: \"Rx += cRy\" means \"add (c times row y) to row x\"");
        System.out.println("    • Subtracting: \"Rx -= cRy\" means \"subtract (c times row y) from row x\"");
        System.out.println("    • Swapping: \"Rx <> Ry\" means \"swap row x with row y\"");

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
        if (Character.toUpperCase(destinationStr.charAt(0)) == 'R') {
            int destinationRow = Integer.parseInt(destinationStr.substring(1)) - 1;

            var matrix = matrixSystem.getMatrix();
            String operation;

            if (destinationRow < 0 || destinationRow >= matrixSystem.getRows()) throw new Exception("invalid destination row");
            String operandStr = split[1].strip();

            if (op.equals("*") || op.equals("/")) {
                if (operandStr.indexOf('r') != -1 || operandStr.indexOf('R') != -1) throw new Exception("Scaling can only be done with scalars");
                Rational<MultivariatePolynomial<BigInteger>> scale = (Rational<MultivariatePolynomial<BigInteger>>) Parser.parse(operandStr);
                if (op.equals("/")) scale = scale.pow(-1);
                for (int i = 0; i < matrix[destinationRow].length; i++) {
                    matrix[destinationRow][i] = matrix[destinationRow][i].multiply(scale);
                }
                operation = "R" + (destinationRow + 1) + " ↦ " + "(" + Printer.rationalToString(scale) + ")R" + (destinationRow + 1);
            }

            else if (op.equals("+") || op.equals("-")) {
                split = operandStr.strip().split("[r,R]");
                Rational<MultivariatePolynomial<BigInteger>> scale;
                int targetRow;
                if (split[0].isEmpty()) {
                    scale = Parser.parse("1");
                }
                else {
                    scale = Parser.parse(split[0]);
                }
                targetRow = Integer.parseInt(split[1]) - 1;
                if (targetRow < 0 || targetRow >= matrix.length) throw new Exception("invalid target row");
                String scaleString = Printer.rationalToString(scale);
                if (op.equals("-")) {
                    scale = scale.multiply(Parser.parse("-1"));
                }
                for (int i = 0; i < matrixSystem.getCols(); i++) {
                    matrix[destinationRow][i] = matrix[destinationRow][i].add(matrix[targetRow][i].multiply(scale));
                }
                operation = "R" + (destinationRow + 1) + " ↦ " + "R" + (destinationRow + 1) + (op.equals("-") ? " - " : " + ")
                        + "(" + scaleString + ")R" + (targetRow + 1);
            }

            else if (op.equals("swap")) {
                split = operandStr.strip().split("[r,R]");
                if (!split[0].isEmpty()) throw new Exception("Cannot scale during swap");
                int targetRow = Integer.parseInt(split[1]) - 1;
                if (targetRow < 0 || targetRow >= matrix.length) throw new Exception("invalid target row");
                var temp = matrix[destinationRow];
                matrix[destinationRow] = matrix[targetRow];
                matrix[targetRow] = temp;
                operation = "R" + (destinationRow + 1) + " ↔ R" + (targetRow + 1);
            }

            else throw new Exception("invalid operation");

            matrixSystem.add(matrix, operation);
        }

        else if (Character.toUpperCase(destinationStr.charAt(0)) == 'C') {
            int destinationCol = Integer.parseInt(destinationStr.substring(1)) - 1;

            var matrix = matrixSystem.getMatrix();
            String operation;

            if (destinationCol < 0 || destinationCol >= matrixSystem.getCols()) throw new Exception("invalid destination column");
            String operandStr = split[1].strip();

            if (op.equals("*") || op.equals("/")) {
                if (operandStr.indexOf('c') != -1 || operandStr.indexOf('C') != -1) throw new Exception("Scaling can only be done with scalars");
                Rational<MultivariatePolynomial<BigInteger>> scale = (Rational<MultivariatePolynomial<BigInteger>>) Parser.parse(operandStr);
                if (op.equals("/")) scale = scale.pow(-1);
                for (int i = 0; i < matrixSystem.getRows(); i++) {
                    matrix[i][destinationCol] = matrix[i][destinationCol].multiply(scale);
                }
                operation = "C" + (destinationCol + 1) + " ↦ " + "(" + Printer.rationalToString(scale) + ")C" + (destinationCol + 1);
            }

            else if (op.equals("+") || op.equals("-")) {
                split = operandStr.strip().split("[c,C]");
                Rational<MultivariatePolynomial<BigInteger>> scale;
                int targetCol;
                if (split[0].isEmpty()) {
                    scale = Parser.parse("1");
                }
                else {
                    scale = Parser.parse(split[0]);
                }
                targetCol = Integer.parseInt(split[1]) - 1;
                if (targetCol < 0 || targetCol >= matrix.length) throw new Exception("invalid target row");
                String scaleString = Printer.rationalToString(scale);
                if (op.equals("-")) {
                    scale = scale.multiply(Parser.parse("-1"));
                }
                for (int i = 0; i < matrixSystem.getRows(); i++) {
                    matrix[i][destinationCol] = matrix[i][destinationCol].add(matrix[i][targetCol].multiply(scale));
                }
                operation = "C" + (destinationCol + 1) + " ↦ " + "C" + (destinationCol + 1) + (op.equals("-") ? " - " : " + ")
                        + "(" + scaleString + ")C" + (targetCol + 1);
            }

            else if (op.equals("swap")) {
                split = operandStr.strip().split("[c,C]");
                if (!split[0].isEmpty()) throw new Exception("Cannot scale during swap");
                int targetCol = Integer.parseInt(split[1]) - 1;
                if (targetCol < 0 || targetCol >= matrix.length) throw new Exception("invalid target row");
                for (int i = 0; i < matrixSystem.getRows(); i++) {
                    var temp = matrix[i][targetCol];
                    matrix[i][targetCol] = matrix[i][destinationCol];
                    matrix[i][destinationCol] = temp;
                }
                operation = "C" + (destinationCol + 1) + " ↔ C" + (targetCol + 1);
            }

            else throw new Exception("invalid operation");

            matrixSystem.add(matrix, operation);
        }

        else throw new Exception("bad formatting");
    }
}
