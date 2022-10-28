public class Tester {
    public static void main(String[] args) {
        Term t = new Term("5 + w");
        Term w = new Term("6");
        System.out.println(t.add(w));
    }
}
