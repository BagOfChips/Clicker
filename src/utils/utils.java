package utils;

import loggerUtils.loggerUtils;

import java.awt.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * utils: utility functions
 */
public class utils{

    private static Logger logger = Logger.getLogger(utils.class.getName());
    static{
        loggerUtils.setLoggerConfig(logger);
    }

    public utils(){
    }

    // generics
    public < E > void shuffleArray(E[] array){
        int randomIndex;
        Random random = new Random();

        for(int i = 0; i < array.length; i++){
            randomIndex = random.nextInt(array.length);
            swap(array, randomIndex, i);
        }
    }

    public < E > void swap(E[] array, int i, int j){
        E temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    // handles int
    public void shuffleArray(int[] array){
        int randomIndex;
        Random random = new Random();

        for(int i = 0; i < array.length; i++){
            randomIndex = random.nextInt(array.length);
            swap(array, randomIndex, i);
        }
    }

    public void swap(int[] array, int i, int j){
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public void printArray(int[] array){
        for(int i: array){
            logger.log(Level.FINEST, "  {0}", i);
        }
    }

    // screen utils
    private Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
    private int screenWidth = (int) screensize.getWidth();
    private int screenHeight = (int) screensize.getHeight();

    public int getScreenWidth(){
        return screenWidth;
    }
    public int getScreenHeight(){
        return screenHeight;
    }

    // randomly select item in array
    private Random random = new Random();

    public int randomPick1numberFromArray(int[] array){
        return array[randomPickNumberInRange(0, array.length - 1)];
    }

    public int randomPickNumberInRange(int lowerBound, int upperBound){
        if(lowerBound > upperBound){
            logger.log(Level.SEVERE, "lowerBound > upperBound; swapping...");
            randomPickNumberInRange(upperBound, lowerBound);
        }
        return random.nextInt((upperBound - lowerBound) + 1) + lowerBound;
    }

    public int[] randomPick2numbersFromArray(int[] array){
        int max = array.length - 1;
        int min = 0;
        return new int[]{array[randomPickNumberInRange(min, max)], array[randomPickNumberInRange(min, max)]};
    }

    public int[] randomPickItemsFromArray(int[] array, int itemsToSelect){
        int[] newArray = new int[itemsToSelect];
        for(int i = 0; i < newArray.length; i++){
            newArray[i] = randomPick1numberFromArray(array);
        }
        return newArray;
    }

}
