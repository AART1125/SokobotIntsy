package solver;

import java.util.Comparator;

public class CostCompare implements Comparator<Node>{
    @Override
    public int compare(Node n1, Node n2){
        if (n1.fValue() == n2.fValue()) {
            return 0;
        } else if (n1.fValue() < n2.fValue()){
            return -1;
        } else 
            return 1;
    }
}
