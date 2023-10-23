package solver;

/**
 * This class represents the coordinates of different items in the game
 */
public class Coordinates {
    private int x, y;// positions of the x and y axis

        /**
         * Contructor for the coordinates
         * @param X x position in the x axis
         * @param Y y position in the y axis
         */
        public Coordinates(int X, int Y){
            this.x = X;
            this.y = Y;
        }

        /**
         * gets the x value of the coordinates
         * @return x
         */
        public int getX() {
            return x;
        }

        /**
         * gets the y value of the coordinates
         * @return y
         */
        public int getY() {
            return y;
        }

}
