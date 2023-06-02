import cc.redberry.rings.Rational;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;

import java.util.ArrayList;
import java.util.Scanner;

public class Controller {
    private static Scanner s;
    private static MatrixCollection matrices;

    public static void main(String[] args) {
        s = new Scanner(System.in);
        Parser.initialize();
        Printer.initialize(args[0]);
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
            deleteMatrix = "Delete a matrix",
            rowReduce = "Perform Gaussian Elimination",
            view = "View matrix",
            exit = "Quit";

    private static final String[] options = new String[]{
            createMatrix,
            deleteMatrix,
            rowOps,
            rowReduce,
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
            case rowReduce -> {
                var matrix = matrixPicker("Pick matrix to row reduce: ");
                if (matrix == null) return;
                MatrixFunctions.rowReduce(matrix);
                Printer.clearConsole();
                System.out.println("Result matrix: ");
                System.out.println(matrix);
                System.out.println("\n---press enter to continue---");
                s.nextLine();
                Printer.clearConsole();
            }
            case guide -> {
                printUserGuide();
            }
            case misc -> {
                MiscFunctionsController.start(matrices, s);
            }
            case createMatrix -> {
                try {
                    matrices.add(s);
                    Printer.clearConsole();
                }
                catch (Exception e) {
                    Printer.clearConsole();
                    if (Printer.runningFromIntelliJ) {
                        e.printStackTrace();
                    }
                    else {
                        System.out.println("Error: " + e.getMessage());
                    }
                    return;
                }
            }
            case view -> {
                var mat = matrixPicker();
                if (mat == null) return;
                Printer.clearConsole();
                var operations =  mat.getOperations().toArray();
                for (int i = 0; i < operations.length; i++) {
                    System.out.println(operations[i] + ": ");
                    System.out.println(mat.toString(i));
                    System.out.println();
                }
                System.out.println("Earlier operations are further up");
                System.out.println("---Press enter to go back---");
                s.nextLine();
                Printer.clearConsole();
            }
            case deleteMatrix -> {
                var deletion = matrixPicker("Choose matrix to delete: ");
                if (deletion != null) {
                    matrices.deleteMatrix(deletion.getName());
                }
                Printer.clearConsole();
            }
        }
    }

    /**
     * @param prompt a string prompt
     * @param f an implementation of MatrixFilter and its isValid() function
     * @return a valid matrix or null. Returning null should go back to main menu
     */

    public static Matrix matrixPicker(String prompt, MatrixFilter f) {
        var arr = matrices.getMatrices();
        ArrayList<Matrix> filteredArr = new ArrayList<>();
        for (Matrix m : arr) {
            if (f.isValid(m)) filteredArr.add(m);
        }
        if (filteredArr.size() == 0) {
            System.out.println("There are no valid matrices.");
            System.out.println("\n---Press enter to go back---");
            s.nextLine();
            Printer.clearConsole();
            return null;
        }
        while (true) {
            for (int i = 0; i < filteredArr.size(); i++) {
                System.out.println((i + 1) + ") " + filteredArr.get(i).preview());
            }
            System.out.print("Type \"quit\" to go back, " + prompt);
            String input = s.nextLine();
            Printer.clearConsole();
            if (input.equals("quit")) return null;
            if (Matrix.isName(input)) {
                var matrix = matrices.get(input);
                if (matrix != null) return matrix;
            }
            else if (!input.equals("")) {
                int index = Integer.parseInt(input) - 1;
                if (index >= 0 && index < filteredArr.size()) return filteredArr.get(index);
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
                System.out.println("Error: invalid name, cannot be \"quit\" or a number");
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
        System.out.println("        Example: \"a,b,c,1,2\" is a valid row entry to a 4x5 matrix, but \"a,b\" is not");
        System.out.println("    3. Naming matrices: Name should start with a letter, entering nothing gives a default name");
        System.out.println("        Default names will be in the format of \"matrix<N>\" and will be unique");
        System.out.println("iii) Errors: errors in menu will be displayed at the top.");
        System.out.println("iv) Matrix selection: When prompted to select matrix, you can enter listing number or name");
        System.out.println("    - Errors: will take you to another page that then asks you to quit or retry");
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
