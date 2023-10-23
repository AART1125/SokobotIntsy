package solver;

import java.util.HashSet;
import java.util.PriorityQueue;

public class SokoBot {

    /**
     * This method will be using the A* Search using the Manhattan Distance of the boxes and the goal as the heuristic
     * @param width - width of the map
     * @param height - height of the map
     * @param mapData - Immovable parts of the map
     * @param itemsData - movable parts of the map
     * @return string representation of the path taken
     */
    public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
        char[] moves = {'u','d','l','r'};
        Node node = new Node(height, width, mapData, itemsData);
        PriorityQueue<Node> openList = new PriorityQueue<Node>(11, new CostCompare());
        HashSet<Node> closedList = new HashSet<Node>();

        openList.add(node);//start the graph (openlist)

        while (!openList.isEmpty()) {//Continue searching while list is not empty
            node = openList.poll();// place head to the current node

            if (node.goalFound()) {
                return node.getPath();//if found, return path
            }

            closedList.add(node);//add node to closed list

            for (char move : moves) {//iterate through each movers
                if (node.isMoveValid(move) && !node.isInSimpleDeadlock()) {//check if move is valid and is not in a deadlock state, if not, skip 
                    Node child = new Node(node, move);//create child
                    
                    if (!closedList.contains(child) || (openList.contains(child) && compareInGraph(openList, child) > child.priorityCosts())) {
                        if (!openList.contains(child)) {
                            openList.add(child);
                        } else {
                            openList.remove(child);
                            openList.add(child);
                        }
                    }
                } 
            }
        }
      return node.getPath() + "\n No solution found";
    }

    /**
     * Checks the queue if there is a node with the same format and return the priority cost
     * @param list - priority queue of node
     * @param node - node being compared
     * @return priority cost of node in queue
     */
    private static int compareInGraph(PriorityQueue<Node> list, Node node){
        
        for (Object item : list.toArray()) {
            if (((Node)item).equals(node)) {
                return ((Node)item).priorityCosts();
            }
        }
        return -1;
    }
}
