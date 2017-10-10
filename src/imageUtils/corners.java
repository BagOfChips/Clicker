package imageUtils;

import java.awt.*;
import java.util.Arrays;

/**
 * Define corners for found image
 * Each corner is a int[] of ONLY 2 values: x and y
 */
public class corners{

    private int[] topLeftCorner;        // 00
    private int[] topRigthCorner;       // 10
    private int[] bottomRightCorner;    // 20
    private int[] bottomLeftCorner;     // 30

    public void printAllCorners(){
        System.out.println("printAllCorners()");
        System.out.println("  topLeft: " + Arrays.toString(topLeftCorner));
        System.out.println("  topRight: " + Arrays.toString(topRigthCorner));
        System.out.println("  bottomRight: " + Arrays.toString(bottomRightCorner));
        System.out.println("  bottomLeft: " + Arrays.toString(bottomLeftCorner));
    }

    public corners(int[] topLeftCorner, int[] topRigthCorner,
                   int[] bottomRightCorner, int[] bottomLeftCorner){
        this.topLeftCorner = topLeftCorner;
        this.topRigthCorner = topRigthCorner;
        this.bottomRightCorner = bottomRightCorner;
        this.bottomLeftCorner = bottomLeftCorner;
    }

    // todo: create a DIFFERENT method that calculates the "center" of the polygon
    // assume for now this will always be a rectangle
    public int[] calculateCenter(){
        int xMiddle = (topRigthCorner[0] + bottomLeftCorner[0]) / 2;
        int yMiddle = (topRigthCorner[1] + bottomLeftCorner[1]) / 2;
        return new int[]{xMiddle, yMiddle};
    }


    public int[] getTopLeftCorner(){
        return topLeftCorner;
    }

    public int[] getTopRigthCorner(){
        return topRigthCorner;
    }

    public int[] getBottomRightCorner(){
        return bottomRightCorner;
    }

    public int[] getBottomLeftCorner(){
        return bottomLeftCorner;
    }

    public void setTopLeftCorner(int[] corner){
        topLeftCorner = corner;
    }

    public void setTopRigthCorner(int[] corner){
        topRigthCorner = corner;
    }

    public void setBottomRightCorner(int[] corner){
        bottomRightCorner = corner;
    }

    public void setBottomLeftCorner(int[] corner){
        bottomLeftCorner = corner;
    }
}
