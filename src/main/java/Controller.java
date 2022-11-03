import java.util.Scanner;

public class Controller {
    private static Scanner s;
    private static MatrixCollection matrices;
    private static final String
            rowOps = "Perform Elementary Operation",
            transpose = "Transpose",
            guide = "User guide",
            createMatrix = "Create new matrix",
            exit = "Quit";

    private static final String[] options = new String[]{
            createMatrix,
            rowOps,
            transpose,
            guide,
            exit
    };

    public static void main(String[] args) {
        s = new Scanner(System.in);
        Parser.initialize();

        while (true) {
            printModes();
            processModes();
        }
    }

    private static void printModes() {
        for (int i = 0; i < options.length; i++) {
            System.out.println((i+1) + ") " + options[i]);
        }
        System.out.println("Choose one of the options (enter a number): ");
    }

    private static void processModes() {
        String str = s.nextLine();
        int choice;
        Printer.clearConsole();
        try {
            choice = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            System.out.println("Error: not a number, please try again");
            return;
        }

        if (choice < 1 || choice > options.length) {
            System.out.println("Error: invalid choice, please try again");
            return;
        }

        switch (options[choice - 1]) {
            case exit -> {
                System.exit(1);
            }
            case rowOps -> {
                OperationsController.start(matrices, s);
            }
            case guide -> {
                System.out.println("wip");
            }
            case transpose -> {
                System.out.println("wip");
            }
            case createMatrix -> {
                try {
                    Initializer.start(matrices, s);
                    Printer.clearConsole();
                }
                catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    return;
                }
            }
        }
    }

    public static Matrix matrixPicker() {
        
    }
}
