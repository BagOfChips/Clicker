package mouseUtils;

import loggerUtils.loggerUtils;
import nmz.nmzController;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Objective:
 *  1. move mouse from current position to given position
 *      - within a given amount of time
 *      - "curved" fashion
 *
 * Reference: https://stackoverflow.com/questions/8534189/making-mouse-movements-humanlike-using-an-arc-rather-than-a-straight-line-to-th
 */
public class moveMouse{

    private static Logger logger = Logger.getLogger(nmzController.class.getName());
    static{
        loggerUtils.setLoggerConfig(logger);
    }

    private Random random = new Random();
    private Robot robot = new Robot();

    /**
     * Constructor
     *
     * @throws AWTException
     */
    public moveMouse() throws AWTException{
    }

    public int getMouseX(){
        return MouseInfo.getPointerInfo().getLocation().x;
    }

    public int getMouseY(){
        return MouseInfo.getPointerInfo().getLocation().y;
    }

    /**
     * @param destX - exact center of targetted image - x coord
     * @param destY - exact center of targetted image - y coord
     * @param xRandomOffset - maximum x offset
     * @param yRandomOffset - maximum y offset
     * @throws AWTException
     */
    public void randomizedMoveMouse(int destX, int destY, int xRandomOffset, int yRandomOffset) throws AWTException{
        randomizedMoveMouse(new Point(getMouseX(), getMouseY()), new Point(destX, destY), xRandomOffset, yRandomOffset);
    }

    public int random(int lowerBound, int upperBound){
        return this.random.nextInt(upperBound - lowerBound + 1) + lowerBound;
    }

    /**
     * @param mouseCurrentPosition - start
     * @param mouseDestination - end (destination)
     * @param xRandomOffset
     * @param yRandomOffset
     */
    private void randomizedMoveMouse(Point mouseCurrentPosition, Point mouseDestination, int xRandomOffset, int yRandomOffset){

        if(Math.abs(mouseDestination.x - mouseCurrentPosition.x) <= xRandomOffset
                && Math.abs(mouseDestination.y - mouseCurrentPosition.y) <= yRandomOffset){
            return;
        }

        Point[] cooardList = new Point[4];
        // set the beginning and end points
        cooardList[0] = mouseCurrentPosition;
        cooardList[3] = new Point(mouseDestination.x + random(-xRandomOffset, xRandomOffset),
                                  mouseDestination.y + random(-yRandomOffset, yRandomOffset));

        int xCurve = Math.abs(mouseDestination.x - mouseCurrentPosition.x) / 10;
        int yCurve = Math.abs(mouseDestination.y - mouseCurrentPosition.y) / 10;

        int x = mouseCurrentPosition.x < mouseDestination.x
                ? mouseCurrentPosition.x + ((xCurve > 0) ? random(1, xCurve) : 1)
                : mouseCurrentPosition.x - ((xCurve > 0) ? random(1, xCurve) : 1);
        int y = mouseCurrentPosition.y < mouseDestination.y
                ? mouseCurrentPosition.y + ((yCurve > 0) ? random(1, yCurve) : 1)
                : mouseCurrentPosition.y - ((yCurve > 0) ? random(1, yCurve) : 1);
        cooardList[1] = new Point(x, y);

        x = mouseDestination.x < mouseCurrentPosition.x
                ? mouseDestination.x + ((xCurve > 0) ? random(1, xCurve) : 1)
                : mouseDestination.x - ((xCurve > 0) ? random(1, xCurve) : 1);
        y = mouseDestination.y < mouseCurrentPosition.y
                ? mouseDestination.y + ((yCurve > 0) ? random(1, yCurve) : 1)
                : mouseDestination.y - ((yCurve > 0) ? random(1, yCurve) : 1);
        cooardList[2] = new Point(x, y);

        double pixelXMouse;
        double pixelYMouse;
        List<Double> pixelXList = new ArrayList<>();
        List<Double> pixelYList = new ArrayList<>();
        // lower k = more generated mouse movements
        double k = .005; // 200 points generated - todo: this should be an input parameter
        for(double t = k; t <= 1 + k; t += k){
            //use Berstein polynomials
            pixelXMouse = (cooardList[0].x + t * (-cooardList[0].x * 3 + t * (3 * cooardList[0].x -
                    cooardList[0].x * t))) + t * (3 * cooardList[1].x + t * (-6 * cooardList[1].x +
                    cooardList[1].x * 3 * t)) + t * t * (cooardList[2].x * 3 - cooardList[2].x * 3 * t) +
                    cooardList[3].x * t * t * t;

            pixelYMouse = (cooardList[0].y + t * (-cooardList[0].y * 3 + t * (3 * cooardList[0].y -
                    cooardList[0].y * t))) + t * (3 * cooardList[1].y + t * (-6 * cooardList[1].y +
                    cooardList[1].y * 3 * t)) + t * t * (cooardList[2].y * 3 - cooardList[2].y * 3 * t) +
                    cooardList[3].y * t * t * t;

            pixelXList.add(pixelXMouse);
            pixelYList.add(pixelYMouse);
        }

        int listSize = pixelXList.size();
        logger.log(Level.FINEST, "Size of mouse coordinates list: {0}", listSize);
        for(int i = 0; i < listSize; i++){
            robot.mouseMove(pixelXList.get(i).intValue(), pixelYList.get(i).intValue());
            robot.delay((int) Math.log((i / 12) + 1)); // todo: this should be an input parameter
        }
    }
}
