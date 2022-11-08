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
            exit = "Quit";

    private static final String[] options = new String[]{
            createMatrix,
            rowOps,
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
                System.out.println("wip");
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
        }
    }

    /**
     *
     * @return a valid matrix or null. Returning null should go back to main menu
     */

    public static Matrix matrixPicker(String prompt) {
        if (matrices.size() == 0) {
            System.out.print("There are no matrices, press enter to go back: ");
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

}
