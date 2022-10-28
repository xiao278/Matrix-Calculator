import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * @author kevin
 * handles the basic math operations of terms
 */

//each char is a different variable i.e. a and A are different things
//how to represent variable's power
//how to represent variable multiplication i.e x*y

public class Subterm {
    //represents the variables in this term and their power
    private HashMap<Character,Integer> variables = new HashMap<>();
    private double coefficient;


    /**
     *
     * @param input coefficient has to be at the front, followed by the variables. This assumes variables are all letters
     *
     */
    /*
    TODO:
        - parse subscript variables
     */
    public Subterm (String input) {
        //separates number and variables
        int numberEndAt = 0;
        for(int i = 0; i < input.length(); i++) {
            if( Character.isLetter(input.charAt(i)) ) {
                numberEndAt = i;
                break;
            }
        }

        int numberStartAt = 0;
        if(!Character.isDigit(input.charAt(0))) numberStartAt = 1;
        int sign = 1;
        if(input.charAt(0) == '-') sign = -1;
        if(numberEndAt-numberStartAt> 0) {
            this.coefficient = Double.parseDouble(input.substring(numberStartAt,numberEndAt))*sign;
        }
        else{
            this.coefficient = 1 * sign;
        }

        boolean inExponent = false;
        for(int i = numberEndAt; i < input.length(); i++) {
            char key = input.charAt(i);
            if(Character.isLetter(key)) {
                if(variables.containsKey(key)) {
                    variables.replace(key,
                            variables.get(key)+1);
                }
                else {
                    variables.put(key,1);
                }
            }
            else {
                if(key == '^') {
                    if(input.charAt(i+1) == '(') {
                        int index = i+2;
                        boolean pairFound = false;
                        String digits = "";
                        while(!pairFound && i < input.length()) {
                            if(input.charAt(index) == ')') {
                                pairFound = true;
                            }
                            else {
                                digits += input.charAt(index);
                            }
                            index++;
                        }
                        int num = Integer.parseInt(digits);
                        variables.replace(input.charAt(i-1),
                                num-1
                                        +variables.get(input.charAt((i-1)))
                        );
                    }
                }
            }
        }
    }

    /**
     * @param coefficient -> a double value
     * @param variables -> take from getVariables
     */
    public Subterm (double coefficient, HashMap<Character,Integer> variables) {
        this.coefficient = coefficient;
        this.variables = variables;
    }

    public HashMap<Character,Integer> getVariables() {
        return variables;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public String toString() {
        DecimalFormat form = new DecimalFormat("#.####");
        String vars = varToString();
        if(Math.abs(coefficient) != 1){
            return coefficient + vars;
        }
        else return vars;

    }

    public Subterm product(Subterm t) {
        return new Subterm(t.getCoefficient()*this.coefficient,
                varProduct(this.variables,t.getVariables()));
    }
    public Subterm product(double c) {
        return new Subterm(coefficient*c,variables);
    }

    /**
     *
     * @param t is the divider
     * @return
     */
    public Subterm quotient(Subterm t) {
        HashMap<Character,Integer> inverted = new HashMap<>();
        for(char key: t.getVariables().keySet()){
            inverted.put(key,t.getVariables().get(key) * (-1));
        }
        return new Subterm(this.coefficient/t.getCoefficient(),
                varProduct(this.variables,inverted));
    }

    /**
     *
     * @param h1 variable hashmap
     * @param h2 variable hashmap
     * @return the product of the two hashmaps;
     */
    public HashMap<Character,Integer> varProduct(HashMap<Character, Integer> h1,
                                                 HashMap<Character, Integer> h2) {

        HashMap<Character,Integer> tempVar = new HashMap<>();
        for(char key: h1.keySet()) {
            tempVar.put(key,h1.get(key));
        }
        for(char key: h2.keySet()) {
            if(tempVar.containsKey(key)) {
                if(tempVar.get(key) + h2.get(key) == 0) {
                    tempVar.remove(key);
                }
                else {
                    tempVar.replace(key,
                            tempVar.get(key) + h2.get(key));
                }
            }
            else {
                tempVar.put(key,h2.get(key));
            }
        }
        return tempVar;
    }
    public String varToString() {
        String vars = "";
        for(char key: variables.keySet()) {
            int value = variables.get(key);

            if(value==1) vars += key;
            else vars += key + "^(" + value + ")";
        }
        return vars;
    }
    public int highestPow() {
        int max = 0;
        for(char key: variables.keySet()) {
            max = Math.max(variables.get(key),max);
        }
        return max;
    }

    public void setVariables(HashMap<Character, Integer> variables) {
        this.variables = variables;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }

    public boolean removeVar(char c) {
        if(variables.containsKey(c)){
            variables.remove(c);
            return true;
        }
        return false;
    }

    public boolean modifyVar(char c, int power) {
        if(variables.containsKey(c)) {
            int temp = variables.get(c) + power;
            if(temp == 0) variables.remove(c);
            else variables.replace(c, temp);
            return true;
        }
        return false;
    }

}
