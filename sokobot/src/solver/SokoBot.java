package solver;

import java.util.PriorityQueue;

public class SokoBot {
  

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    
     char[] moves = {'u','d','l','r'};
     String result = "uuddlrlr"; //resulting moves the robot needs to do to solve puzzle
     Node root = new Node(height, width, mapData, itemsData);
     PriorityQueue<Node> frontier = new PriorityQueue<Node>(new CostCompare());
     PriorityQueue<Node> explored = new PriorityQueue<Node>();
     
     frontier.add(root);
     if (frontier.contains(root)) {
      
      System.out.println("Root exists");
     } else {
      System.out.println("Error!!! Root not found in queue");
     }

    return result;
  }

}
