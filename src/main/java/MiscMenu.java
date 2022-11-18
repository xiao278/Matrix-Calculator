import java.util.Scanner;

public class MiscMenu {
    private static Scanner s;
    private static MatrixCollection matrices;

    public static void start(MatrixCollection matrixCollection, Scanner scanner) {
        s = scanner;
        matrices = matrixCollection;
        while (true) {
            printModes();
            if (processModes()) return;
        }
    }

    private static final String
        product = "Matrix Multipliation",
        transpose = "Matrix Transpose",
        quit = "Go back",
        determinant = "Matrix Determinant";

    private static final String[] options = new String[]{
            product,
            transpose,
            determinant,
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
                int inner = A.getCols();
                Matrix B = Controller.matrixPicker("Pick right matrix (" + inner + "xAny): ");
                if (B == null) return false;
                var product = MatrixFunctions.product(A, B);
                if (product != null) {
                    matrices.add(Controller.namePicker(product, "enter product matrix name: "));
                    Printer.clearConsole();
                    System.out.println("product:");
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
                matrices.add(Controller.namePicker(mt, "enter transposed matrix name: "));
                Printer.clearConsole();
                System.out.println("transposed matrix: ");
                Printer.printMatrix(mt);
                System.out.println("\n---press enter to continue---");
                s.nextLine();
                Printer.clearConsole();
            }
            case quit -> {
                return true;
            }
            case determinant -> {
                var mat = Controller.matrixPicker("enter a square matrix: ");
                if (mat == null) return false;
                var det = MatrixFunctions.findDeterminant(mat);
                if (det == null) return false;
                System.out.println("matrix determinant: ");
                System.out.println(Printer.rationalToString(det));
                System.out.println("\n---press enter to continue---");
                s.nextLine();
                Printer.clearConsole();
            }
        }
        return false;
    }
}
