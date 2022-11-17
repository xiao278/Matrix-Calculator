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
        quit = "Go back";

    private static final String[] options = new String[]{
            product,
            transpose,
            quit
    };

    private static void printModes() {
        for (int i = 0; i < options.length; i++) {
            System.out.println((i+1) + ") " + options[i]);
        }
        System.out.print("Choose one of the options (enter a number): ");
    }

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
                Matrix A = Controller.matrixPicker("Pick left matrix:");
                if (A == null) return false;
                int inner = A.getCols();
                Matrix B = Controller.matrixPicker("Pick right matrix (" + inner + "xN): ");
                if (B == null) return false;
                var product = Multiplication.product(A, B);
                if (product != null) {
                    matrices.add(Controller.namePicker(product, "enter product matrix name: "));
                    Printer.clearConsole();
                    System.out.println("product:");
                    Printer.printMatrix(product);
                    System.out.println("press enter to continue");
                    s.nextLine();
                    Printer.clearConsole();
                }
                return false;
            }
            case transpose -> {
                System.out.println("wip");
            }
            case quit -> {
                return true;
            }
        }
        return false;
    }
}
