package solver;

import java.util.HashSet;
import java.util.PriorityQueue;

public class SokoBot {

    /**
     * This method will be using the A* Search using the Manhattan Distance of the boxes and the goal
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
        int gen = 0;

        openList.add(node);//start the graph (openlist)

        while (!openList.isEmpty()) {//Continue searching while list is not empty
            node = openList.poll();// place head to the current node

            if (node.goalFound()) {
                return node.getPath();//if found, return path
            }

            closedList.add(node);//add node to closed list

            for (char move : moves) {//iterate through each movers
                if (node.isMoveValid(move) ) {//check if move is valid, if not, skip
                    Node child = new Node(node, move);//create child
                    gen++;
                    //System.out.println(gen);
                    //System.out.println(move + " " + gen + " " + node.getHeuristicCost());
                    /*for (int i = 0; i < height; i++) {
                        System.out.print(child.getItems()[i][0]);
                        for (int j = 0; j < width; j++) {
                            System.out.print(child.getItems()[i][j]);
                        }
                        System.out.println("");
                    }*/

                    if (!openList.contains(child) && ! closedList.contains(child)) {//add child to open list
                        openList.add(child);                    
                    } else if (openList.contains(child) && compareInGraph(openList, child) > child.priorityCosts()){//check if child is present in list, if yes, remove and readd child for queuing
                        openList.remove(child);
                        openList.add(child);
                    }
                } 
            }
        }
      return node.getPath() + "\n No solution found";
    }

    private static int compareInGraph(PriorityQueue<Node> list, Node node){
        for (Object item : list.toArray()) {
            if (((Node)item).equals(node)) {
                return ((Node)item).priorityCosts();
            }
        }
        return -1;
    }
}
