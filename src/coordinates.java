/**
 * coordinates: store (x, y) values
 */
public class coordinates{

    private int x;
    private int y;

    public coordinates(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getxCoordinate(){
        return x;
    }

    public int getyCoordinate(){
        return y;
    }

    public void printCoordinates(){
        System.out.println("x: " + x);
        System.out.println("y: " + y);
    }
}
