package solver;

import java.lang.Math;
import java.util.ArrayList;

/**
 * Represents the current/possible states of the game. Goal of the game is to put all boxes in its 
 * rightful position, therefore, a state or node should contain the information of the map i.e player position,
 * box position, target position, and the cost of the state. This class is using the comparable interface
 * in order to be used for the priority queue class in searching for the optimal path for the bot.
 */
public class Node{

    private Coordinates player;//Current position of the player in the map
    private Coordinates[] boxes, target;// Current positions of the boxes and targets in the map
    private ArrayList<Coordinates> deadlockPosition;// gets the deadlocks in the map
    private char[][] map, items;//Representation of map and the movable items within it
    private int actualCost, heuristicCost;//Costs of this state
    private int height, width;// height and width of the map
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
        this.height = height;
        this.width = width;
        this.player = playerPosition(itemsData);
        this.boxes = boxPosition(itemsData);
        this.target = targetPosition(mapData);
        this.deadlockPosition = deadlocks(mapData);
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
    public Node(Node parentNode, char move) {
        this.height = parentNode.getHeight();
        this.width = parentNode.getWidth();
        this.parentNode = parentNode;
        this.deadlockPosition = parentNode.getDeadlockPosition();
        this.actualCost = parentNode.getActualCost() + 1;
        this.path = parentNode.getPath() + move;
        this.map = parentNode.getMap();
 
        char[][] newMap= newItemState(parentNode, move);
        this.items = new char[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                this.items[i][j] = newMap[i][j];
            }
        }
        this.player = playerPosition(this.items);
        this.boxes = boxPosition(this.items);
        this.target = parentNode.getTarget();

        this.heuristicCost = calculateHeuristicCost();
    }

    //reads the position of target items
    private Coordinates[] targetPosition(char[][] mapData){
        int targetcount = 0, poscount = 0;
        int[] posX = new int[11], posY = new int[11];

        //checks map for targets
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (mapData[i][j] == '.' || mapData[i][j] == '*') {
                   posX[poscount] = i;
                   posY[poscount] = j;
                   poscount++;

                   targetcount++;
                }
            }
        }

        //create array of coordinates
        Coordinates[] positions = new Coordinates[targetcount];

        for (int i = 0; i < poscount; i++) {
            positions[i] = new Coordinates(posX[i], posY[i]);
        }

        return positions;
    }

    //reads the position of the boxes
    private Coordinates[] boxPosition(char[][] itemsData){
        int boxcount = 0, poscount = 0;
        int[] posX = new int[11], posY = new int[11];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (itemsData[i][j] == '$' || itemsData[i][j] == '*') {
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
    private Coordinates playerPosition(char[][] itemsData){

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (itemsData[i][j] == '@' || itemsData[i][j] == '+') {
                   return new Coordinates(i, j);
                }
            }
        }

        return null;
    }

    private ArrayList<Coordinates> deadlocks(char[][] mapData){
        ArrayList<Coordinates> positions = new ArrayList<Coordinates>();

        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                boolean corner = true;
                for (int k = -1; k <= 1; k++) {
                    for (int l = -1; l <= 1; l++) {
                        if (mapData[i+k][j+l] != '#') {
                            corner = false;
                            break;
                        }
                    }
                    if (!corner) {
                        break;
                    }
                }
                if (corner) {
                    positions.add(new Coordinates(i, j));
                }
            }
        }

        return positions;
    }

    //Calculates the current heuristic cost of the state using the Manhattan Distance
    private int calculateHeuristicCost(){
        int cost = 0, min = 0, dist = 0;

        for (Coordinates boxes : boxes) {
            min = 100;
            for (Coordinates target : target) {
                dist = Math.abs(boxes.getX() - target.getX()) + Math.abs(boxes.getY() - target.getY());
                if (dist < min) {
                    min = dist;
                }
            }
            cost += min;
        }
        
        return cost;
    }

    /**
     * Checks if the goal was found by checking the map and item attributes of the object
     * @return true or false
     */
    /*
     * Originally : (boxes[i].getX() == target[i].getX()) && (boxes[i].getY() == boxes[i].getY()) // works
     * Changed to : (boxes[i].getX() == target[i].getX()) && (boxes[i].getY() == target[i].getY()) // does not work
     * Correct logic but queue becomes empty before completion
     */
    public boolean goalFound(){
        int goalcount = 0;
        for (int i = 0; i < target.length; i++) {
            if ((boxes[i].getX() == target[i].getX()) && (boxes[i].getY() == target[i].getY())) {
                goalcount++;
            }
        }
        return goalcount == target.length;
        //return heuristicCost == 0;
    }

    //Creates a new state based on the direction it decided to go to
    /*
     * REQUIRED TO FIX 
     */
    private char[][] newItemState(Node prev, char move){
        char[][] newState = new char[height][width];//initialize the original state to change accordingly

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                newState[i][j] = prev.getItems()[i][j];
            }
        }

        
        switch (move) {
            case 'u':
                if ((map[prev.getPlayer().getX() - 1][prev.getPlayer().getY()] == ' ' ||
                    map[prev.getPlayer().getX() - 1][prev.getPlayer().getY()] == '.') &&
                    newState[prev.getPlayer().getX() - 1][prev.getPlayer().getY()] != '$') {

                    newState[prev.getPlayer().getX() - 1][prev.getPlayer().getY()] = '@';

                }

                else if (newState[prev.getPlayer().getX() - 1][prev.getPlayer().getY()] == '$') {
                    this.actualCost++;

                    if (map[prev.getPlayer().getX() - 2][prev.getPlayer().getY()] == ' ' ||
                        map[prev.getPlayer().getX() - 2][prev.getPlayer().getY()] == '.') {
                        
                        newState[prev.getPlayer().getX() - 1][prev.getPlayer().getY()] = '@';
                        newState[prev.getPlayer().getX() - 2][prev.getPlayer().getY()] = '$';
                    } 

                }
                
                newState[prev.getPlayer().getX()][prev.getPlayer().getY()] = ' ';

                break;
            

            case 'd':
                if ((map[prev.getPlayer().getX() + 1][prev.getPlayer().getY()] == ' ' ||
                    map[prev.getPlayer().getX() + 1][prev.getPlayer().getY()] == '.') &&
                    newState[prev.getPlayer().getX() + 1][prev.getPlayer().getY()] != '$') {

                    newState[prev.getPlayer().getX() + 1][prev.getPlayer().getY()] = '@';

                }

                else if (newState[prev.getPlayer().getX() + 1][prev.getPlayer().getY()] == '$') {
                    this.actualCost++;

                    if (map[prev.getPlayer().getX() + 2][prev.getPlayer().getY()] == ' ' ||
                        map[prev.getPlayer().getX() + 2][prev.getPlayer().getY()] == '.') {
                        
                        newState[prev.getPlayer().getX() + 1][prev.getPlayer().getY()] = '@';
                        newState[prev.getPlayer().getX() + 2][prev.getPlayer().getY()] = '$';
                    } 

                }
                
                newState[prev.getPlayer().getX()][prev.getPlayer().getY()] = ' ';
                
                break;

            case 'l':
                if ((map[prev.getPlayer().getX()][prev.getPlayer().getY() - 1] == ' ' ||
                    map[prev.getPlayer().getX()][prev.getPlayer().getY() - 1] == '.') &&
                    newState[prev.getPlayer().getX()][prev.getPlayer().getY() - 1] != '$') {

                    newState[prev.getPlayer().getX()][prev.getPlayer().getY() - 1] = '@';

                } 
                
                else if (newState[prev.getPlayer().getX()][prev.getPlayer().getY() - 1] == '$') {
                    this.actualCost++;

                    if (map[prev.getPlayer().getX()][prev.getPlayer().getY() - 2] == ' ' ||
                        map[prev.getPlayer().getX()][prev.getPlayer().getY() - 2] == '.') {
                        
                        newState[prev.getPlayer().getX()][prev.getPlayer().getY() - 1] = '@';
                        newState[prev.getPlayer().getX()][prev.getPlayer().getY() - 2] = '$';
                    } 

                }
                
                newState[prev.getPlayer().getX()][prev.getPlayer().getY()] = ' ';
                
                break;

            case 'r':
                if ((map[prev.getPlayer().getX()][prev.getPlayer().getY() + 1] == ' ' ||
                    map[prev.getPlayer().getX()][prev.getPlayer().getY() + 1] == '.')  &&
                    newState[prev.getPlayer().getX()][prev.getPlayer().getY() + 1] != '$') {

                    newState[prev.getPlayer().getX()][prev.getPlayer().getY() + 1] = '@';

                }

                else if (newState[prev.getPlayer().getX()][prev.getPlayer().getY() + 1] == '$') {
                    this.actualCost++;

                    if (map[prev.getPlayer().getX()][prev.getPlayer().getY() + 2] == ' ' ||
                        map[prev.getPlayer().getX()][prev.getPlayer().getY() + 2] == '.') {
                        
                        newState[prev.getPlayer().getX()][prev.getPlayer().getY() + 1] = '@';
                        newState[prev.getPlayer().getX()][prev.getPlayer().getY() + 2] = '$';
                    } 

                }
                
                newState[prev.getPlayer().getX()][prev.getPlayer().getY()] = ' ';

                break;
        }
        return newState;
         
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

    /**
     * Checks for deadlock results
     * @param move direction of the movement
     * @return true or false
     */
    public boolean isDeadloack(char move) {
        switch (move) {
            case 'u':
                for (Coordinates corner : deadlockPosition) {
                    for (Coordinates boxes : boxes) {
                        if (((boxes.getX() - 2) == corner.getX()) && (boxes.getY() == corner.getY())) {
                            return true;
                        }
                    }
                    
                }
                return false;
            
            case 'd':
                for (Coordinates corner : deadlockPosition) {
                    for (Coordinates boxes : boxes) {
                        if (((boxes.getX() + 2) == corner.getX()) && (boxes.getY() == corner.getY())) {
                            return true;
                        }
                    }
                }
                return false;
            
            case 'l':
                for (Coordinates corner : deadlockPosition) {
                    for (Coordinates boxes : boxes) {
                        if ((boxes.getX() == corner.getX()) && ((boxes.getY() - 2) == corner.getY())) {
                            return true;
                        }
                    }
                }
                return false;
            
            case 'r':
                for (Coordinates corner : deadlockPosition) {
                    for (Coordinates boxes : boxes) {
                        if ((boxes.getX() == corner.getX()) && ((boxes.getY() + 2) == corner.getY())) {
                            return true;
                        }
                    }
                }
                return false;
        }
        return true;
    }


    public boolean isMoveValid(char move) {
        char pos, pos2, item, item2;
        switch (move) {
            case 'u':
                pos = map[this.getPlayer().getX() - 1][this.getPlayer().getY()];
                item = this.getItems()[this.getPlayer().getX() - 1][this.getPlayer().getY()];

                if (pos == '#') {
                    return false;
                } else {
                    pos2 = map[this.getPlayer().getX() - 2][this.getPlayer().getY()];
                    item2 = this.getItems()[this.getPlayer().getX() - 2][this.getPlayer().getY()];

                    if (item == '$' && item2 != '$' && pos2 != '#') {
                        return true;
                    } else if (item == ' ') {
                        return true;
                    } else {
                        return false;
                    }
                    
                }
            case 'd':
                pos = map[this.getPlayer().getX() + 1][this.getPlayer().getY()];
                item = this.getItems()[this.getPlayer().getX() + 1][this.getPlayer().getY()];

                if (pos == '#') {
                    return false;
                } else {
                    pos2 = map[this.getPlayer().getX() + 2][this.getPlayer().getY()];
                    item2 = this.getItems()[this.getPlayer().getX() + 2][this.getPlayer().getY()];

                    if (item == '$' && item2 != '$' && pos2 != '#') {
                        return true;
                    } else if (item == ' ') {
                        return true;
                    } else {
                        return false;
                    }
                    
                }
            case 'l':
                pos = map[this.getPlayer().getX()][this.getPlayer().getY() - 1];
                item = this.getItems()[this.getPlayer().getX()][this.getPlayer().getY() - 1];

                if (pos == '#') {
                    return false;
                } else {
                    pos2 = map[this.getPlayer().getX()][this.getPlayer().getY() - 2];
                    item2 = this.getItems()[this.getPlayer().getX()][this.getPlayer().getY() - 2];

                    if (item == '$' && item2 != '$' && pos2 != '#') {
                        return true;
                    } else if (item == ' ') {
                        return true;
                    } else {
                        return false;
                    }
                    
                }
            case 'r':
                pos = map[this.getPlayer().getX()][this.getPlayer().getY() + 1];
                item = this.getItems()[this.getPlayer().getX()][this.getPlayer().getY() + 1];

                if (pos == '#') {
                    return false;
                } else {
                    pos2 = map[this.getPlayer().getX()][this.getPlayer().getY() + 2];
                    item2 = this.getItems()[this.getPlayer().getX()][this.getPlayer().getY() + 2];

                    if (item == '$' && item2 != '$' && pos2 != '#') {
                        return true;
                    } else if (item == ' ') {
                        return true;
                    } else {
                        return false;
                    }
                    
                }
        }
        return false;
    }

    @Override
    public int hashCode(){
        return getItems().hashCode();
    }
    
    /**
     * Checks if the object (Node) has an equal string rep of the current node
     * @param obj Object to be compared
     * @return true or false
     */
    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof Node)) {
            return false;
        }

        if (((Node)obj).stringRep().equals(this.stringRep())) {
            return true;
        } else 
            return false;
        
    }

    /**
     * gets the sum of the actual cost and heuristic cost
     * @return sum of the actual cost and heuristic cost
     */
    public int fValue(){
        return getActualCost() + getHeuristicCost();
    }
    
    /**
     * gets the height of the map
     * @return height
     */
    public int getHeight() {
        return height;
    }

    /**
     * gets the width of the map
     * @return width
     */
    public int getWidth() {
        return width;
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

    public ArrayList<Coordinates> getDeadlockPosition() {
        return deadlockPosition;
    }

}