//
public class Parser {
    public static String parseInput(String in) {
        if (in.length() <= 1) {
            return in;
        }
        StringBuilder out = new StringBuilder();
        out.append(in.charAt(0));
        for (int i = 1; i < in.length(); i++) {
            char prevChar = in.charAt(i-1);
            char curChar = in.charAt(i);
            if ((Character.isLetter(prevChar) && Character.isLetter(curChar)) ||
                    (Character.isDigit(prevChar) && Character.isLetter(curChar)) ||
                    (Character.isLetter(prevChar) && Character.isDigit(curChar))
            ) {
                out.append("*");
            }
            out.append(curChar);
        }
        return out.toString();
    }
}
