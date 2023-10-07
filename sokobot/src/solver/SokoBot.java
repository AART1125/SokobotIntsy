package solver;

import java.util.HashSet;
import java.util.PriorityQueue;

public class SokoBot {
  

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
      char[] moves = {'u','d','l','r'};
      String result = "dldr"; //resulting moves the robot needs to do to solve puzzle
      Node state = new Node(height, width, mapData, itemsData);
      PriorityQueue<Node> frontier = new PriorityQueue<Node>(new CostCompare());
      HashSet<String> explored = new HashSet<String>();
      int gen = 0;
      frontier.add(state);

      while (true) {
          if ((frontier.peek()) == null){
              return "ududududuudududuudduudududuudududududu";
          }

          state = frontier.poll();

          if (state.goalFound()) 
              return state.getPath();

          explored.add(state.stringRep());

          for (char move : moves) {
            gen++;
            System.out.println(gen + " " + move);
            Node child = new Node(state, move);

            for (int i = 0; i < child.getItems().length; i++) {
              System.out.print(child.getItems()[i]);
                for (int j = 0; j < child.getItems()[0].length; j++) {
                  System.out.print(child.getItems()[i][j]);
                }
              System.out.println("");
            }

            System.out.println(frontier.contains(child));
            System.out.println(explored.contains(child.stringRep()));

            if (!frontier.contains(child) && !explored.contains(child.stringRep())) {
              frontier.add(child); 
              System.out.println("Added state!");
            } else if (frontier.contains(child)) {
              int MDofPQ = 0;

              for (Object prev : frontier.toArray()) {
                if (((Node)prev).equals(child)) {
                  MDofPQ = ((Node)prev).getHeuristicCost() + ((Node)prev).getActualCost();
                } else {
                  MDofPQ = -1;
              }

              if (MDofPQ > child.fValue()){
                frontier.remove(child);
                frontier.add(child);
              } 
            }
          } else if (explored.contains(child.stringRep())){
            System.out.println("repeat");
          }
        }
      }
    }
}
