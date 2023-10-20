package solver;

import java.util.HashSet;
import java.util.PriorityQueue;

public class SokoBot {
  

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
      char[] moves = {'u','d','l','r'}; //resulting moves the robot needs to do to solve puzzle
      Node state = new Node(height, width, mapData, itemsData);
      PriorityQueue<Node> frontier = new PriorityQueue<Node>(new CostCompare());
      HashSet<String> explored = new HashSet<String>();
      int gen = 0;
      frontier.add(state);

      while (true) {
          if (frontier.peek() == null) {
            return "uddudududuududududuudududududududududuududududududuudududududuuddu";
          }
          state = frontier.poll();

          
          if (state.goalFound()) {
            return state.getPath();
          }
              
          explored.add(state.stringRep());
          
          for (char move : moves) {
            
            Node child = new Node(state, move);   
            gen++;  
            //System.out.println(move + " " + gen + " " + state.getHeuristicCost());
            /*for (int i = 0; i < height; i++) {
              System.out.print(child.getItems()[i][0]);
              for (int j = 0; j < width; j++) {
                System.out.print(child.getItems()[i][j]);
              }
              System.out.println();
            }*/

            if (!frontier.contains(child) && !explored.contains(child.stringRep())) {
              //System.out.println("added");
              frontier.add(child); 
              
            } else if(frontier.contains(child) && getStarMDFromPQ(frontier, child) > child.fValue()){
              frontier.remove(child);
              frontier.add(child);
            } else {
              //System.out.println("repeat");
            } 
            }
          } 
          
        }
      
        private int getStarMDFromPQ(PriorityQueue<Node> pq, Node comp) {
            for(Object orig : pq.toArray()) {
              if( ((Node) orig).equals(comp) ){
                return ((Node)orig).fValue();
            }
           
          }
           return -1;
        }}
    