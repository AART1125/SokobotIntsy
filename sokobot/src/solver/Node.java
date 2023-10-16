package solver;

import java.lang.Math;

/**
 * Represents the current/possible states of the game. Goal of the game is to put all boxes in its 
 * rightful position, therefore, a state or node should contain the information of the map i.e player position,
 * box position, target position, and the cost of the state. This class is using the comparable interface
 * in order to be used for the priority queue class in searching for the optimal path for the bot.
 */
public class Node implements Comparable<Node>{

    private Coordinates player;//Current position of the player in the map
    private Coordinates[] boxes, target;// Current positions of the boxes and targets in the map
    private char[][] map, items;//Representation of map and the movable items within it
    private int actualCost, heuristicCost;//Costs of this state
    private int goals;//found goals
    private Node parentNode;//Node connected to the state
    private String path;// A string representation of the path taken to get to the current state

    /**
     * Constructor for the root node
     * @param height height of the map
     * @param width width of the map
     * @param mapData the representation of the immovable objects in the map
     * @param itemsData the representation of movable objects in the map
     */
    public Node(int height, int width, char[][] mapData, char[][] itemsData) {
        this.player = playerPosition(height, width, itemsData);
        this.boxes = boxPosition(height, width, itemsData);
        this.goals = 0;
        this.target = targetPosition(height, width, mapData);
        this.map = mapData;
        this.items = itemsData;
        this.actualCost = 0;
        this.heuristicCost = calculateHeuristicCost();
        this.parentNode = null;
        this.path = "";
    }

    /**
     * Constructor for the child node based on parent nodes desicion
     * @param height height of the map
     * @param width width of the map
     * @param parentNode the parent node of the state
     * @param move the move that was performed
     */
    public Node(int height, int width, Node parentNode, char move) {
        
        this.parentNode = parentNode;
        this.actualCost = parentNode.getActualCost() + 1;
        this.path = parentNode.getPath() + move;
        this.map = parentNode.getMap();
 
        char[][] newMap= newItemState(height, width, parentNode, move);
        this.items = new char[newMap.length][newMap[0].length];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                this.items[i][j] = newMap[i][j];
            }
        }
        this.goals = 0;
        this.player = playerPosition(height, width, this.items);
        this.boxes = boxPosition(height, width, this.items);
        this.target = parentNode.getTarget();

        this.heuristicCost = calculateHeuristicCost();
    }

    //reads the position of target items
    private Coordinates[] targetPosition(int height, int width,char[][] mapData){
        int targetcount = 0, poscount = 0;
        int[] posX = new int[11], posY = new int[11];

        //checks map for targets
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (mapData[i][j] == '.') {
                   posX[poscount] = i;
                   posY[poscount] = j;
                   poscount++;

                   targetcount++;
                }
            }
        }

        //create array of coordinates
        Coordinates[] positions = new Coordinates[targetcount];
        this.goals = targetcount;

        for (int i = 0; i < poscount; i++) {
            positions[i] = new Coordinates(posX[i], posY[i]);
        }

        return positions;
    }

    //reads the position of the boxes
    private Coordinates[] boxPosition(int height, int width,char[][] itemsData){
        int boxcount = 0, poscount = 0;
        int[] posX = new int[11], posY = new int[11];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (itemsData[i][j] == '$') {
                   posX[poscount] = i;
                   posY[poscount] = j;
                   poscount++;

                   boxcount++;
                }
            }
        }

        Coordinates[] positions = new Coordinates[boxcount];

        for (int i = 0; i < poscount; i++) {
            positions[i] = new Coordinates(posX[i], posY[i]);
        }

        return positions;
    }
    
    //reads the position of the player
    private Coordinates playerPosition(int height, int width, char[][] itemsData){

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (itemsData[i][j] == '@') {
                   return new Coordinates(i, j);
                }
            }
        }

        return null;
    }

    //Calculates the current heuristic cost of the state using the Manhattan Distance
    private int calculateHeuristicCost(){
        int cost = 0, min = 0, dist = 0;

        for (Coordinates boxes : boxes) {
            min = 100;
            for (Coordinates target : target) {
                dist = Math.abs(boxes.getX() - target.getX()) + Math.abs(boxes.getY() - target.getY());
                min = Math.min(min, dist);
            }
            cost += min;
        }
        
        return cost;
    }

    /**
     * Checks if the goal was found by checking the map and item attributes of the object
     * @return true or false
     */
    public boolean goalFound(){
        int goalCount = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == items[i][j]){
                    goalCount++;
                }
            }
        }

        if (goalCount == goals) {
            return true;
        }

        return false;
    }

    //Creates a new state based on the direction it decided to go to
    private char[][] newItemState(int height, int width, Node state, char move){
        char[][] newState = new char[height][width];//initialize the original state to change accordingly

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                newState[i][j] = state.getItems()[i][j];
            }
        }

        if (isMoveValid(state, move)) {
            switch (move) {
            case 'u':
                if (newState[state.getPlayer().getX() - 1][state.getPlayer().getY()] == ' ' ||
                map[state.getPlayer().getX() - 1][state.getPlayer().getY()] == '.') {

                newState[state.getPlayer().getX() - 1][state.getPlayer().getY()] = '@';

                } else if (newState[state.getPlayer().getX() - 1][state.getPlayer().getY()] == '$') {

                    actualCost++;
                    
                    if (newState[state.getPlayer().getX() - 2][state.getPlayer().getY()] == ' ' ||
                        map[state.getPlayer().getX() - 2][state.getPlayer().getY()] == '.') {

                        newState[state.getPlayer().getX() - 2][state.getPlayer().getY()] = '$';
                        newState[state.getPlayer().getX() - 1][state.getPlayer().getY()] = '@';
                    }
                }
            
                if(newState[state.getPlayer().getX()][state.getPlayer().getY()] == '@')
                    newState[state.getPlayer().getX()][state.getPlayer().getY()] = ' ';

                return newState;
            
            
            case 'd':
                if (newState[state.getPlayer().getX() + 1][state.getPlayer().getY()] == ' ' ||
                    map[state.getPlayer().getX() + 1][state.getPlayer().getY()] == '.') {

                    newState[state.getPlayer().getX() + 1][state.getPlayer().getY()] = '@';

                } else if (newState[state.getPlayer().getX() + 1][state.getPlayer().getY()] == '$') {

                    actualCost++;
                    
                    if (newState[state.getPlayer().getX() + 2][state.getPlayer().getY()] == ' ' ||
                        map[state.getPlayer().getX() + 2][state.getPlayer().getY()] == '.') {

                        newState[state.getPlayer().getX() + 2][state.getPlayer().getY()] = '$';
                        newState[state.getPlayer().getX() + 1][state.getPlayer().getY()] = '@';
                    }
                }
                
                if(newState[state.getPlayer().getX()][state.getPlayer().getY()] == '@')
                    newState[state.getPlayer().getX()][state.getPlayer().getY()] = ' ';

                return newState;
            
            
            case 'l':
                if (newState[state.getPlayer().getX()][state.getPlayer().getY() - 1] == ' ' ||
                    map[state.getPlayer().getX()][state.getPlayer().getY() - 1] == '.') {

                    newState[state.getPlayer().getX()][state.getPlayer().getY() - 1] = '@';

                } else if (newState[state.getPlayer().getX()][state.getPlayer().getY() - 1] == '$') {

                    actualCost++;
                    
                    if (newState[state.getPlayer().getX()][state.getPlayer().getY() - 2] == ' ' ||
                        map[state.getPlayer().getX()][state.getPlayer().getY() - 2] == '.') {

                        newState[state.getPlayer().getX()][state.getPlayer().getY() - 2] = '$';
                        newState[state.getPlayer().getX()][state.getPlayer().getY() - 1] = '@';
                    }
                }
                
                if(newState[state.getPlayer().getX()][state.getPlayer().getY()] == '@')
                    newState[state.getPlayer().getX()][state.getPlayer().getY()] = ' ';

                return newState;
            
            
            case 'r':
                if (newState[state.getPlayer().getX()][state.getPlayer().getY() + 1] == ' ' ||
                    map[state.getPlayer().getX()][state.getPlayer().getY() + 1] == '.') {

                    newState[state.getPlayer().getX()][state.getPlayer().getY() + 1] = '@';

                } else if (newState[state.getPlayer().getX()][state.getPlayer().getY() + 1] == '$') {

                    actualCost++;
                    
                    if (newState[state.getPlayer().getX()][state.getPlayer().getY() + 2] == ' ' ||
                        map[state.getPlayer().getX()][state.getPlayer().getY() + 2] == '.') {

                        newState[state.getPlayer().getX()][state.getPlayer().getY() + 2] = '$';
                        newState[state.getPlayer().getX()][state.getPlayer().getY() + 1] = '@';
                    }
                }
                
                if(newState[state.getPlayer().getX()][state.getPlayer().getY()] == '@')
                    newState[state.getPlayer().getX()][state.getPlayer().getY()] = ' ';
                
                return newState;
            
            }
        } 
        
        return newState;
    }

    /**
     * {@Javadoc}
     * Overidden function of the comparable interface
     */
    @Override
    public int compareTo(Node node) {
        if (this.actualCost == node.getActualCost()) {
            return 0;
        } else if (this.actualCost < node.getActualCost()) {
            return 1;
        }

        return -1;
    }

    /**
     * Creates the string representation of the map for checking
     * @return map string
     */
    public String stringRep(){
        String str = "";
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == '#' || map[i][j] == '.') {
                    str += map[i][j];
                } else {
                    str += items[i][j];
                }
            }
        }
        return str;
    }


    private boolean isMoveValid(Node prev, char move) {
        switch (move) {
            case 'u':
                if (map[prev.getPlayer().getX() - 1][prev.getPlayer().getY()] != '#') {
                    return true;
                } else {
                    return false;
                }
            case 'd':
                if (map[prev.getPlayer().getX() + 1][prev.getPlayer().getY()] != '#') {
                    return true;
                } else {
                    return false;
                }
            case 'l':
                if (map[prev.getPlayer().getX()][prev.getPlayer().getY() - 1] != '#') {
                    return true;
                } else {
                    return false;
                }
                
            case 'r':
                if (map[prev.getPlayer().getX()][prev.getPlayer().getY() + 1] != '#') {
                    return true;
                } else {
                    return false;
                }
            default:
                return false;
        }
    }

    /**
     * Checks if the object (Node) has an equal string rep of the current node
     * @param obj Object to be compared
     * @return true or false
     */
    public boolean equal(Object obj){
        if (obj == null || !(obj instanceof Node)) {
            return false;
        }

        if (((Node)obj).stringRep().equals(this.stringRep())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * gets the sum of the actual cost and heuristic cost
     * @return sum
     */
    public int fValue(){
        return actualCost + heuristicCost;
    }
    
    /**
     * gets the coordinates of the boxes
     * @return array of position of boxes
     */
    public Coordinates[] getBoxes() {
        return boxes;
    }

    /**
     * gets the actual cost of the node
     * @return actual cost
     */
    public int getActualCost() {
        return actualCost;
    }

    /**
     * gets the heuristic cost of the node
     * @return heuristic cost
     */
    public int getHeuristicCost() {
        return heuristicCost;
    }

    /**
     * gets the number of goals in the node
     * @return goals
     */
    public int getGoals() {
        return goals;
    }

    /**
     * gets the mapping of the movable items in the node
     * @return movable items
     */
    public char[][] getItems() {
        return items;
    }

    /**
     * gets the main map of the node
     * @return main map
     */
    public char[][] getMap() {
        return map;
    }

    /**
     * gets the string path of what the current node has took
     * @return current path representation
     */
    public String getPath() {
        return path;
    }

    /**
     * gets the parent node of the node
     * @return parent node
     */
    public Node getParentNode() {
        return parentNode;
    }

    /**
     * gets the coordinates of the player on the map
     * @return postion of player
     */
    public Coordinates getPlayer() {
        return player;
    }

    /**
     * gets the coordinates of the targets in the map
     * @return array of possitions of targets
     */
    public Coordinates[] getTarget() {
        return target;
    }

}