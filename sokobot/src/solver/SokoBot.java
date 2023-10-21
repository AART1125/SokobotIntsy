package solver;

import java.util.HashSet;
import java.util.PriorityQueue;

public class SokoBot {

    public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
        char[] moves = {'u','d','l','r'};
        Node node = new Node(height, width, mapData, itemsData);
        PriorityQueue<Node> frontier = new PriorityQueue<>(11, new CostCompare());
        HashSet<Node> explored = new HashSet<Node>();
        int gen = 0;

        frontier.add(node);

        while (!frontier.isEmpty()) {
            node = frontier.poll();

            if (node.goalFound()) {
                System.out.println(node.getPath());
                return node.getPath();
            }

            explored.add(node);

            for (char move : moves) {
                if (node.isMoveValid(move)) {
                    Node child = new Node(node, move);
                    gen++;
                    if (!child.isFreezeDeadloack()) {
                        //System.out.println(gen);
                        //System.out.println(move + " " + gen + " " + node.getHeuristicCost());
                        /*for (int i = 0; i < height; i++) {
                            System.out.print(child.getItems()[i][0]);
                            for (int j = 0; j < width; j++) {
                                System.out.print(child.getItems()[i][j]);
                            }
                            System.out.println("");
                        }*/

                        if (!frontier.contains(child) && ! explored.contains(child)) {
                            frontier.add(child);
                            //System.out.println("State Added");                    
                        } else if (frontier.contains(child) && compareInTree(frontier, child) > child.fValue()){
                            frontier.remove(child);
                            frontier.add(child);
                        } else {
                            //System.out.println("State Repeated");
                        }
                    }
                } 
            }
        }
      return node.getPath();
    }

    private static int compareInTree(PriorityQueue<Node> pq, Node node){
        for (Object item : pq.toArray()) {
            if (((Node)item).equals(node)) {
                return ((Node)item).fValue();
            }
        }
        return -1;
    }
}
