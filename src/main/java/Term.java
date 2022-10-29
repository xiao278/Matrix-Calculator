import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

public class Term {
    private ArrayList<Subterm> termList;
    private ArrayList<Subterm> denominator;
    private double constant;

    public Term (String input) {
        //remove all spaces
        termList = new ArrayList<>();
        denominator = new ArrayList<>();
        input = input.replaceAll("\\s","");
        int termStartIndex = 0;
        boolean hasVar = false;
        for(int i = 0; i <= input.length(); i++) {
            if(i < input.length() && Character.isLetter(input.charAt(i))) {
                hasVar = true;
            }
            if((i == input.length() || input.charAt(i) == '+' || input.charAt(i) == '-')
                && i-termStartIndex > 0)
            {
                if(hasVar == true) {
                    termList.add(new Subterm(input.substring(termStartIndex,i)));
                }
                else {
                    constant += Double.parseDouble(input.substring(termStartIndex,i));
                }
                termStartIndex = i;
                hasVar = false;
            }
        }
    }
    public Term (ArrayList<Subterm> t, double constant) {
        this.termList = t;
        this.constant = constant;
    }

    public void setList(ArrayList<Subterm> arr) {
        this.termList = arr;
    }
    public ArrayList<Subterm> getList() {
        return termList;
    }

    public String toString() {
        DecimalFormat form = new DecimalFormat("#.####");

        String out = "";
        for(int i = 0; i < termList.size(); i++) {
            double coef = termList.get(i).getCoefficient();
            if(i > 0 && coef > 0) {
                out += " + ";
            }
            else if(coef < 0) {
                out += " - ";
            }
            if(Math.abs(coef) != 1) out += form.format(Math.abs(coef));
            out += termList.get(i).varToString();
        }
        if(constant != 0) {
            if(termList.size() > 0) {
                if(constant > 0) {
                    out += " + ";
                }
                else out += " - ";
            }
            out += form.format(Math.abs(constant));
        }
        return out;
    }

    public Term add(Term t){
        //variables, coefficient
        ArrayList<Subterm> arr = new ArrayList<>();

        for(int i = 0; i < termList.size(); i++) {
            arr.add(termList.get(i));
        }
        for(int i = 0; i < t.termList.size(); i++) {
            arr.add(t.termList.get(i));
        }
        double constantSum = t.constant + this.constant;
        return new Term(arr,constantSum).simplify();
    }

    public Term product(Term t) {
        ArrayList<Subterm> arr = new ArrayList<>();
        for(Subterm s1 : termList) {
            for(Subterm s2 : t.termList) {
                arr.add(s1.product(s2));
            }
        }
        if(constant != 0) {
            for(Subterm s2 : t.termList) {
                arr.add(s2.product(constant));
            }
        }
        if(t.constant != 0) {
            for(Subterm s1 : termList) {
                arr.add(s1.product(t.constant));
            }
        }
        double constantProd = t.constant*constant;
        return new Term(arr,constantProd).simplify();
    }

    public Term product(String s) {
        return this.product(new Term(s));
    }

    /**
     *
     * @param exp >= 1
     * @return Term that's raised to exp
     */
    public Term pow(int exp) {
        if(exp == 1){
            return this;
        }
        else if (exp < 0){
            System.err.println("Term.pow: Out of domain");
            System.exit(1);
        }
        return pow(exp-1).product(this);
    }

    //TODO 0 coefficient glitch when multiple variable terms are multiplied
    public Term simplify () {
        HashMap<String,Double> subterms = new HashMap<>();
        ArrayList<Subterm> a = this.getList();
        for(int i = 0; i < a.size(); i++) {
            Subterm s = a.get(i);
            String key = s.varToString();
            if(subterms.containsKey(key)){
                if(subterms.get(key) + s.getCoefficient() == 0) subterms.remove(key);
                else subterms.replace(key, subterms.get(key) + s.getCoefficient());
            }
            else {
                subterms.put(key,s.getCoefficient());
            }
        }
        ArrayList<Subterm> tempTermList = new ArrayList<>();
        for(String key : subterms.keySet()) {
            tempTermList.add(new Subterm(subterms.get(key) + key));
        }
        return new Term(tempTermList,this.constant);
    }

    public Term sortPower(){
        ArrayList<Subterm> arr = this.getList();
        for(int i = arr.size()-1; i >= 0; i--){
            for(int j = 0; j < i; j++) {
                int hPow = arr.get(j).highestPow();
                int hPow1 = arr.get(j+1).highestPow();
                if (hPow < hPow1) {
                    Collections.swap(arr,j,j+1);
                }
            }
        }
        return new Term(arr,this.constant);
    }


    /**
     *
     * @param factor factor by a single variable, like x
     * @return whether factoring was successful or not
     */
    public boolean factor(char factor) {
        for(Subterm s: termList) {
            if(!s.getVariables().containsKey(factor)) {
                return false;
            }
        }
        for(Subterm s: termList) {
            s.modifyVar(factor, -1);
        }
        return true;
    }
}
