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
     * @param parentNode the parent node of the state
     */
    public Node(Node parentNode, char move) {
        
        this.parentNode = parentNode;
        this.actualCost = parentNode.getActualCost() + 1;
        this.path = parentNode.getPath() + move;
        this.map = parentNode.getMap();
 
        char[][] newMap= newItemState(parentNode, move);
        this.items = new char[newMap.length][];
        for (int i = 0; i < newMap.length; i++) {
            this.items[i] = new char[newMap[i].length];
            for (int j = 0; j < newMap[0].length; j++) {
                this.items[i][j] = newMap[i][j];
            }
        }

        this.player = playerPosition(this.map.length, this.map[0].length, this.items);
        this.boxes = boxPosition(this.map.length, this.map[0].length, this.items);
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
    private char[][] newItemState(Node state, char move){
        char[][] newState = new char[state.getItems().length][];//initialize the original state to change accordingly

        for (int i = 0; i < newState.length; i++) {
            newState[i] = new char[state.getItems()[i].length];
            for (int j = 0; j < newState.length; j++) {
                newState[i][j] = state.getItems()[i][j];
            }
        }

        Coordinates playerPosition = state.getPlayer();

        if (playerPosition != null && isMoveValid(state, move)) {
            switch (move) {
            case 'u':
                System.out.println("can move up");
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
            
                newState[state.getPlayer().getX()][state.getPlayer().getY()] = ' ';

                return newState;
            
            
            case 'd':
                System.out.println("can move down");
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
                
                newState[state.getPlayer().getX()][state.getPlayer().getY()] = ' ';

                return newState;
            
            
            case 'l':
                System.out.println("can move left");
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
                
                newState[state.getPlayer().getX()][state.getPlayer().getY()] = ' ';

                return newState;
            
            
            case 'r':
                System.out.println("can move right");
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
                
                newState[state.getPlayer().getX()][state.getPlayer().getY()] = ' ';
                
                return newState;
            
            }
        } else {
            System.out.println("invalid move");
        }
        
        return newState;
    }

    //Overided method of Comparable interface
    @Override
    public int compareTo(Node node) {
        if (this.actualCost == node.getActualCost()) {
            return 0;
        } else if (this.actualCost < node.getActualCost()) {
            return 1;
        }

        return -1;
    }

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

    //Checks if the possible movement is a valid one or not
    public boolean isMoveValid(Node prev,char move) {
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

    // Calculates the f function value of the node
    public int fValue(){
        return actualCost + heuristicCost;
    }
    
    public Coordinates[] getBoxes() {
        return boxes;
    }

    public int getActualCost() {
        return actualCost;
    }

    public int getHeuristicCost() {
        return heuristicCost;
    }

    public char[][] getItems() {
        return items;
    }

    public char[][] getMap() {
        return map;
    }

    public String getPath() {
        return path;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public Coordinates getPlayer() {
        return player;
    }

    public Coordinates[] getTarget() {
        return target;
    }

}