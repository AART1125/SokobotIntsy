package solver;

import java.util.Comparator;

/**
 * This java class uses the <code>Comparator</code> interface to be used in the <code>PriorityQueue</code> class by comparing the priority cost of each node
 */
public class CostCompare implements Comparator<Node>{
    @Override
    public int compare(Node n1, Node n2){
        if (n1.priorityCosts() > n2.priorityCosts()) {
            return 1;
        } else if (n1.priorityCosts() < n2.priorityCosts()){
            return -1;
        }

        return 0;
    }
}
