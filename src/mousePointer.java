import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

/**
 * mousePointer: cursor utilities
 */
public class mousePointer{

    private Point position;
    private Robot robot = new Robot();

    private Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
    private int screenWidth = (int) screensize.getWidth();
    private int screenHeight = (int) screensize.getHeight();

    private int radius;
    private coordinates[] coordinates;
    private bounds xClickingBounds;
    private bounds yClickingBounds;

    // to generate random cursor location
    private int xRandom;
    private int yRandom;
    private Random random = new Random();
    private int coordinatesArrayLength;

    public mousePointer() throws AWTException{
        Properties prop = new Properties();
        InputStream input = null;

        try{
            input = new FileInputStream("config/config.properties");
            prop.load(input);

            int radius
                    = Integer.parseInt(prop.getProperty("radius"));
            this.radius = radius;

            coordinatesArrayLength
                    = Integer.parseInt(prop.getProperty("coordinatesArrayLength"));
            coordinates = new coordinates[coordinatesArrayLength];

            setCurrentMousePosition();
            generateRandomizedCoordinatesArray(generateclicksIntervalAtPoint());

        }catch(IOException e){
            e.printStackTrace();
        }finally{
            if(input != null){
                try{
                    input.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public int generateclicksIntervalAtPoint(){
        return random.nextInt((coordinatesArrayLength - 1) + 1) + 1;
    }

    private void setCurrentMousePosition(){
        position = MouseInfo.getPointerInfo().getLocation();
    }

    public void moveMouseWithinRadius(coordinates coordinates){
        int x = coordinates.getxCoordinate();
        int y = coordinates.getyCoordinate();
        moveMouse(x, y);
    }

    // testing only
    public void printCoordinatesArray(){
        for(int i = 0; i < coordinates.length; i++){
            System.out.println("Coordinate: " + i);
            coordinates[i].printCoordinates();
        }
    }

    public void generateRandomizedCoordinatesArray(int clicksIntervalAtPoint){
        if(xClickingBounds == null || yClickingBounds == null){
            generateClickingBounds();
        }
        generateRandomCoordinate();

        // use radius to generate random coordinates / bounds
        for(int i = 0; i < coordinates.length; i++){
            // generate random x's and y's within bounds
            if(i == 0 || i % clicksIntervalAtPoint == 0){
                generateRandomCoordinate();
            }
            coordinates[i] = new coordinates(xRandom, yRandom);
        }
        //printCoordinatesArray();
    }

    // after clicking bounds are set, generate random (x, y) coordinates
    private void generateRandomCoordinate(){
        xRandom = random.nextInt(
                (xClickingBounds.getUpperBound() - xClickingBounds.getLowerBound()) + 1)
                + xClickingBounds.getLowerBound();
        yRandom = random.nextInt(
                (yClickingBounds.getUpperBound() - yClickingBounds.getLowerBound()) + 1)
                + yClickingBounds.getLowerBound();
    }

    private void generateClickingBounds(){
        int x = position.x;
        int y = position.y;

        xClickingBounds = new bounds(x - radius, x + radius);
        yClickingBounds = new bounds(y - radius, y + radius);

        // case 1: x lower bound < 0
        if(xClickingBounds.getLowerBound() < 0){
            xClickingBounds.setLowerBound(0);
        }

        // case 2: x upper bound > screen width
        if(xClickingBounds.getUpperBound() > screenWidth){
            xClickingBounds.setUpperBound(screenWidth);
        }

        // case 3: y lower bound < 0
        if(yClickingBounds.getLowerBound() < 0){
            yClickingBounds.setLowerBound(0);
        }

        // case 4: y upper bound > screen height
        if(yClickingBounds.getUpperBound() > screenHeight){
            yClickingBounds.setUpperBound(screenHeight);
        }
    }

    private void moveMouse(int x, int y){
        robot.mouseMove(x, y);
    }

    public coordinates[] getCoordinatesArray(){
        return coordinates;
    }
}
