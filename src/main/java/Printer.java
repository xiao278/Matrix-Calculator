import cc.redberry.rings.Rational;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;

import java.util.Arrays;

/**
 * class related to printing to console
 */
public class Printer {
    //matrix column padding
    public static boolean runningFromIntelliJ;

    public static void initialize(String ide) {
        if (ide.equals("IDE")) runningFromIntelliJ = true;
        else runningFromIntelliJ = false;
    }

    public static void clearConsole()
    {
        if (runningFromIntelliJ) {
            //a bunch of new line
            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        } else {
            //actual console clear for when not in IDE console
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (Exception e) {
                //do nothing
            }
        }
    }

}
