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
    private Node parentNode;//Node connected to the state

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
    }

    /**
     * Constructor for the child node
     * @param parentNode the parent node of the state
     */
    public Node(Node parentNode) {
        this.parentNode = parentNode;

        this.player = parentNode.getPlayer();
        this.boxes = parentNode.getBoxes();
        this.target = parentNode.getTarget();

        this.map = parentNode.getMap();
        this.items = itemsData;
        this.actualCost = parentNode.getActualCost() + 1;
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
                dist = Math.abs(boxes.getPosX() - target.getPosX()) + Math.abs(boxes.getPosY() - target.getPosY());
                min = Math.min(min, dist);
            }
            cost += min;
        }
        
        return cost;
    }

    //Creates a new state based on the direction it decided to go to
    private char[][] newState(Node state){
        return null;
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

    //Checks if the possible movement is a valid one or not
    public boolean isMoveValid(char move) {
        switch (move) {
            case 'u':
                if (map[player.getPosX() - 1][player.getPosY()] == '#' ||
                    map[player.getPosX() - 2].length <= player.getPosY()) {
                    return false;
                } else {
                    return true;
                }
            case 'd':
                if (map[player.getPosX() + 1][player.getPosY()] == '#' ||
                    map[player.getPosX() + 2].length <= player.getPosY()) {
                    return false;
                } else {
                    return true;
                }
            case 'l':
                if (map[player.getPosX()][player.getPosY() - 1] == '#' ||
                    player.getPosY() <= 1) {
                    return false;
                } else {
                    return true;
                }
                
            case 'r':
                if (map[player.getPosX()][player.getPosY() + 1] == '#' ||
                    map[player.getPosX() + 2].length <= player.getPosY() + 2) {
                    return false;
                } else {
                    return true;
                }
            default:
                return false;
        }
    }

    public Node updateNewState() {
        return null;
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