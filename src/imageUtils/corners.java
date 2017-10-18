package imageUtils;

import loggerUtils.loggerUtils;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Define corners for found image
 * Each corner is a int[] of ONLY 2 values: x and y
 */
public class corners{

    private static Logger logger = Logger.getLogger(corners.class.getName());
    static{
        /*logger = */loggerUtils.setLoggerConfig(logger);
    }

    private int[] topLeftCorner;        // 00
    private int[] topRightCorner;       // 10
    private int[] bottomRightCorner;    // 20
    private int[] bottomLeftCorner;     // 30

    public void printAllCorners(){
        logger.log(Level.FINER, "  Top left corner: {0}", Arrays.toString(topLeftCorner));
        logger.log(Level.FINER, "  Top right corner: {0}", Arrays.toString(topRightCorner));
        logger.log(Level.FINER, "  Bottom right left corner: {0}", Arrays.toString(bottomRightCorner));
        logger.log(Level.FINER, "  Bottom left corner: {0}", Arrays.toString(bottomLeftCorner));
    }

    public corners(int[] topLeftCorner, int[] topRightCorner,
                   int[] bottomRightCorner, int[] bottomLeftCorner){
        this.topLeftCorner = topLeftCorner;
        this.topRightCorner = topRightCorner;
        this.bottomRightCorner = bottomRightCorner;
        this.bottomLeftCorner = bottomLeftCorner;
    }

    // todo: create a DIFFERENT method that calculates the "center" of the polygon
    // assume for now this will always be a rectangle
    public int[] calculateCenter(){
        int xMiddle = (topRightCorner[0] + bottomLeftCorner[0]) / 2;
        int yMiddle = (topRightCorner[1] + bottomLeftCorner[1]) / 2;
        return new int[]{xMiddle, yMiddle};
    }


    public int[] getTopLeftCorner(){
        return topLeftCorner;
    }

    public int[] getTopRightCorner(){
        return topRightCorner;
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

    public void setTopRightCorner(int[] corner){
        topRightCorner = corner;
    }

    public void setBottomRightCorner(int[] corner){
        bottomRightCorner = corner;
    }

    public void setBottomLeftCorner(int[] corner){
        bottomLeftCorner = corner;
    }
}
