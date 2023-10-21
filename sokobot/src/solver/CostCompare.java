package solver;

import java.util.Comparator;

public class CostCompare implements Comparator<Node>{
    @Override
    public int compare(Node n1, Node n2){
        if (n1.getHeuristicCost() == n2.getHeuristicCost()) {
            return 0;
        } else if (n1.getHeuristicCost() < n2.getHeuristicCost()){
            return -1;
        }
        return 1;
    }
}
