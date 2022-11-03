import java.util.HashMap;

//class to hold a collection of matrices
public class MatrixCollection {
    private HashMap<String, Matrix> hashMap;
    public MatrixCollection () {
        hashMap = new HashMap<>();
    }

    /**
     *
     * @param input get name of matrix
     * @return
     */
    public Matrix select(String input) {
        return hashMap.get(input);
    }

    public boolean contains(String input) {
        return hashMap.containsKey(input);
    }

    public void add(Matrix m) {
        hashMap.put(m.getName(), m);
    }
}
