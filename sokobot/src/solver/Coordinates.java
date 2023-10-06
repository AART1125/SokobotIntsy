package solver;

public class Coordinates {
    private int posX, posY;

        public Coordinates(int X, int Y){
            this.posX = X;
            this.posY = Y;
        }

        public int getPosX() {
            return posX;
        }

        public int getPosY() {
            return posY;
        }

        public void setPosX(int posX) {
            this.posX = posX;
        }

        public void setPosY(int posY) {
            this.posY = posY;
        }
}
