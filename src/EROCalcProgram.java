import java.util.Arrays;
import java.util.Scanner;

public class EROCalcProgram {
    final static int padding = 0;
    public static void main(String[] args) throws Exception {
        //[row][col]
        Term[][] matrix;
        int row;
        int col;
        Scanner s = new Scanner(System.in);
        clearConsole();
        System.out.print("dimension of matrix? <row,col>: ");
        var buffer = s.nextLine();
        String[] splitBuffer = buffer.split(",");
        if (splitBuffer.length != 2) {
            System.out.println("wrong input");
            return;
        }
        clearConsole();
        row = Integer.parseInt(splitBuffer[0]);
        col = Integer.parseInt(splitBuffer[1]);
        matrix = new Term[row][col];
        for (int i = 0; i < row; i++) {
            System.out.print("input for row " + (i+1) + " (split entries with <,>):");
            buffer = s.nextLine();
            splitBuffer = buffer.split(",");
            if (splitBuffer.length != col) {
                System.out.println("wrong input");
                return;
            }
            for (int j = 0; j < col; j++) {
                matrix[i][j] = new Term(splitBuffer[j]);
            }
        }
        printMatrix(matrix);
    }

    //bruh
    public static void clearConsole()
    {
        //a bunch of new line
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

    private static void printMatrix(Term[][] matrix) {
        String[][] toStringMatrix = new String[matrix.length][matrix[0].length];

        //max length for each column
        int[] columnLength = new int[matrix[0].length];
        Arrays.fill(columnLength, 0);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                toStringMatrix[i][j] = matrix[i][j].toString();
                columnLength[j] = Math.max(columnLength[j], toStringMatrix[i][j].length() + padding);
            }
        }

        for (int i = 0; i < matrix.length; i++) {
            StringBuilder out = new StringBuilder("[ ");
            out.append(" ".repeat(padding));
            for (int j = 0; j < matrix[i].length; j++) {
                out.append(toStringMatrix[i][j]);
                int spaceNeeded = columnLength[j] - toStringMatrix[i][j].length();
                out.append(" ".repeat(Math.max(0, spaceNeeded)));
                if (j < matrix[i].length - 1) out.append(",  ");
            }
            out.append(" ]");
            System.out.println(out);
        }
    }
}
