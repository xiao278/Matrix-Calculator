import cc.redberry.rings.Rational;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;

import java.util.Scanner;

public class Controller {
    private static Scanner s;
    private static MatrixCollection matrices;

    public static void main(String[] args) {
        s = new Scanner(System.in);
        Parser.initialize();
        matrices = new MatrixCollection();
        Printer.clearConsole();

        while (true) {
            printModes();
            processModes();
        }
    }

    private static final String
            rowOps = "Perform Elementary Operation",
            misc = "Misc. Functions",
            guide = "User guide",
            createMatrix = "Create new matrix",
            view = "View matrix",
            exit = "Quit";

    private static final String[] options = new String[]{
            createMatrix,
            rowOps,
            view,
            misc,
            guide,
            exit
    };

    private static void printModes() {
        for (int i = 0; i < options.length; i++) {
            System.out.println((i+1) + ") " + options[i]);
        }
        System.out.print("Choose one of the options (enter a number): ");
    }

    private static void processModes() {
        String str = s.nextLine();
        int choice;
        Printer.clearConsole();
        try {
            choice = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            System.out.println("Error: not a number");
            return;
        }

        if (choice < 1 || choice > options.length) {
            System.out.println("Error: invalid choice");
            return;
        }

        switch (options[choice - 1]) {
            case exit -> {
                System.exit(1);
            }
            case rowOps -> {
                var matrix = matrixPicker();
                if (matrix == null) return;
                OperationsController.start(matrix, s);
            }
            case guide -> {
                printUserGuide();
            }
            case misc -> {
                MiscMenu.start(matrices, s);
            }
            case createMatrix -> {
                try {
                    Initializer.start(matrices, s);
                    Printer.clearConsole();
                }
                catch (Exception e) {
                    Printer.clearConsole();
                    System.out.println("Error: " + e.getMessage());
                    return;
                }
            }
            case view -> {
                var mat = matrixPicker();
                if (mat == null) return;
                Printer.clearConsole();
                var states = mat.getMatrixStates();
                var operations =  mat.getOperations().toArray();
                for (int i = 0; i < operations.length; i++) {
                    System.out.println(operations[i] + ": ");
                    Printer.printMatrix(states.get(i));
                    System.out.println();
                }
                System.out.println("Earlier additions are further up");
                System.out.println("---Press enter to go back---");
                s.nextLine();
                Printer.clearConsole();
            }
        }
    }

    /**
     *
     * @return a valid matrix or null. Returning null should go back to main menu
     */

    public static Matrix matrixPicker(String prompt, MatrixFilter f) {
        if (matrices.size() == 0) {
            System.out.println("There are no matrices.");
            System.out.println("---Press enter to go back---");
            s.nextLine();
            Printer.clearConsole();
            return null;
        }
        while (true) {
            var arr = matrices.getMatrices();
            for (int i = 0; i < arr.length; i++) {
                System.out.println((i + 1) + ") " + arr[i].preview());
            }
            System.out.print(prompt);
            String input = s.nextLine();
            Printer.clearConsole();
            var matrix = matrices.get(input);
            if (matrix == null) {
                try {
                    var index = Integer.parseInt(input) - 1;
                    matrix = arr[index];
                    return matrix;
                }
                catch (NumberFormatException e) {
                    System.out.println("Error: invalid input");
                    System.out.println("press enter to retry, type \"q\" to quit: ");
                    input = s.nextLine();
                    Printer.clearConsole();
                    if (input.equals("q")) return null;
                }
                catch (IndexOutOfBoundsException e) {
                    System.out.println("Error: invalid number");
                    System.out.println("press enter to retry, type \"q\" to quit: ");
                    input = s.nextLine();
                    Printer.clearConsole();
                    if (input.equals("q")) return null;
                }
            }
            else {
                    return matrix;
            }
        }
    }

    public static Matrix matrixPicker() {
        return matrixPicker("enter matrix name or listing number: ");
    }

    public static Matrix matrixPicker(String prompt) {
        return matrixPicker(prompt, new MatrixFilter() {
            @Override
            public boolean isValid(Matrix m) {
                return true;
            }
        });
    }

    /**
     *
     * @param prompt
     * @return null if default name requested, otherwise return desired name
     */
    public static Matrix namePicker(Rational<MultivariatePolynomial<BigInteger>>[][] squareArr, String prompt) {
        String name;
        while (true) {
            System.out.print(prompt);
            name = s.nextLine().strip();
            Printer.clearConsole();
            if (name.isEmpty()) {
                return new Matrix(squareArr);
            }
            if (Matrix.isName(name)) {
                if (matrices.contains(name)) System.out.println("Error: duplicate naming");
                else {
                    break;
                }
            }
            else {
                System.out.println("Error: invalid name, first character cannot be a number");
            }
        }
        return new Matrix(squareArr, name);
    }

    public static Matrix namePicker(Rational<MultivariatePolynomial<BigInteger>>[][] squareArr) {
        return namePicker(squareArr,"enter new matrix name: ");
    }

    private static void printUserGuide() {
        System.out.println("i) Important: at least one matrix must be created using option 1 in main menu before using the other functions");
        System.out.println("ii) Creating new matrix: ");
        System.out.println("    1. Entering dimensions: Formatting is mxn where m=#rows n=#cols");
        System.out.println("        Example: an input of \"3x4\" means a matrix of 3 rows and 4 columns.");
        System.out.println("    2. Entering rows: You will enter each row at once separated by commas");
        System.out.println("        Example: \"a,b,c,1,2\" is a valid row entry to a 4x5 matrix, but \"a,b\" is not because it only has 2 entries.");
        System.out.println("    3. Naming matrices: Name should start with a letter, entering nothing gives a default name");
        System.out.println("        Default names will be in the format of \"matrix<N>\" where N will increment for each additional default-name matrix.");
        System.out.println("iii) Errors: errors will be displayed at the top. The topmost line usually tells you formatting and choice errors");
        System.out.println("\n---Press enter to go back---");
        s.nextLine();
        Printer.clearConsole();
    }
}

interface MatrixFilter {
    default boolean isValid(Matrix m) {
        return true;
    }
}
