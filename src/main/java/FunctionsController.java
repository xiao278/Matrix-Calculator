import cc.redberry.rings.Rational;
import java.util.Scanner;

public class FunctionsController {
    private static Scanner s;
    private static MatrixCollection matrices;

    public static void start(MatrixCollection matrixCollection, Scanner scanner) {
        s = scanner;
        matrices = matrixCollection;
        while (true) {
            printModes();
            if (processModes()) return;
            Printer.clearConsole();
        }
    }

    private static final String
        product = "Matrix Multipliation",
        transpose = "Matrix Transpose",
        quit = "Go back",
        addition = "Matrix Addition",
        determinant = "Matrix Determinant",
        solve = "Solve";

    private static final String[] options = new String[]{
            addition,
            product,
            transpose,
            determinant,
            solve,
            quit
    };

    private static void printModes() {
        for (int i = 0; i < options.length; i++) {
            System.out.println((i+1) + ") " + options[i]);
        }
        System.out.print("Choose one of the options (enter a number): ");
    }

    /**
     *
     * @return true if user chooses quit option
     */
    private static boolean processModes() {
        String str = s.nextLine();
        int choice;
        Printer.clearConsole();
        try {
            choice = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            System.out.println("Error: not a number");
            return false;
        }

        if (choice < 1 || choice > options.length) {
            System.out.println("Error: invalid choice");
            return false;
        }

        switch (options[choice - 1]) {
            case product -> {
                Matrix A = Controller.matrixPicker("Pick left matrix: ");
                if (A == null) return false;
                int inner = A.cols;
                Matrix B = Controller.matrixPicker("Pick right matrix: ", new MatrixFilter() {
                    @Override
                    public boolean isValid(Matrix m) {
                        return m.rows == inner;
                    }
                });
                if (B == null) return false;
                var product = MatrixFunctions.product(A, B);
                if (product != null) {
                    matrices.add(Controller.namePicker(product, "Enter name for new product matrix: "));
                    Printer.clearConsole();
                    System.out.println("Product:");
                    Printer.printMatrix(product);
                    System.out.println("\n---press enter to continue---");
                    s.nextLine();
                    Printer.clearConsole();
                }
                return false;
            }
            case transpose -> {
                Matrix m = Controller.matrixPicker("Pick matrix to be transposed: ");
                if (m == null) return false;
                var mt = MatrixFunctions.transpose(m);
                matrices.add(Controller.namePicker(mt, "Enter transposed matrix name: "));
                Printer.clearConsole();
                System.out.println("Transposed matrix: ");
                Printer.printMatrix(mt);
                System.out.println("\n---press enter to continue---");
                s.nextLine();
            }
            case quit -> {
                return true;
            }
            case determinant -> {
                var mat = Controller.matrixPicker("Pick a square matrix: ", new MatrixFilter() {
                    @Override
                    public boolean isValid(Matrix m) {
                        return (m.rows == m.cols);
                    }
                });
                if (mat == null) return false;
                var det = MatrixFunctions.findDeterminant(mat);
                if (det == null) return false;
                System.out.println("Matrix determinant: ");
                System.out.println(Printer.rationalToString(det));
                System.out.println("\n---press enter to continue---");
                s.nextLine();
            }
            case addition -> {
                var left = Controller.matrixPicker("Pick a matrix: ");
                if (left == null) return false;
                var right = Controller.matrixPicker("Pick another matrix: ", new MatrixFilter() {
                    @Override
                    public boolean isValid(Matrix m) {
                        return left.cols == m.cols && left.rows == m.rows;
                    }
                });
                if (right == null) return false;
                System.out.print("enter an integer coefficient for second matrix: ");
                String coefStr = s.nextLine();
                int coef;
                try {
                    coef = Integer.parseInt(coefStr);
                }
                catch (NumberFormatException e) {
                    return false;
                }
                var result = MatrixFunctions.matrixAdd(left, right, coef);
                Printer.clearConsole();
                matrices.add(Controller.namePicker(result, "Enter name for result matrix: "));
                Printer.clearConsole();
                System.out.println("Result matrix: ");
                Printer.printMatrix(result);
                System.out.println("\n---press enter to continue---");
                s.nextLine();
            }
            case solve -> {
                var matrix = Controller.matrixPicker();
                if (matrix == null) return false;
                Rational[][] solution;
                try {
                    Printer.printMatrix(matrix.getMatrixCopy());
                    System.out.println("Separate entries with comas,");
                    System.out.print("Enter a " + matrix.rows + "x1 transposed column vector: ");
                    var split = s.nextLine().split(",");
                    if (split.length != matrix.rows) return false;
                    Rational[] b = new Rational[matrix.rows];
                    for (int i = 0; i < b.length; i++) {
                        b[i] = Parser.parse(split[i]);
                    }
                    solution = MatrixFunctions.solve(matrix, b);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                Printer.clearConsole();
                if (solution == null) {
                    System.out.println("System is inconsistent");
                }
                System.out.println("\n---press enter to continue---");
                s.nextLine();
            }
        }
        return false;
    }
}
