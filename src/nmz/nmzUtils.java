package nmz;

import imageUtils.corners;
import imageUtils.targettedImage;
import loggerUtils.loggerUtils;
import mouseUtils.mouseClick;
import mouseUtils.moveMouse;
import org.opencv.core.Mat;
import timerUtils.timer;
import utils.bounds;
import imageUtils.snapshot;
import imageUtils.imageSURF;
import utils.msArrays;
import utils.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * "Controller"
 */
public class nmzUtils{

    private static Logger logger = Logger.getLogger(nmzController.class.getName());
    static{
        logger = loggerUtils.setLoggerConfig(logger);
    }

    /**
     * Not automated:
     *  1. logging in
     *  2. enter bank key
     *  3. guzzle to 1 hp (and staying at 1 hp)
     *  4. entering nmz
     *  5. drinking up to 1000 absorption
     *  6. running to corner
     *  7. logging out
     */

    // timers
    public timer prayerFlickTimer;
    public timer drinkAbsorptionTimer;
    public timer drinkSuperRangingTimer;
    public bounds endTimeBounds;
    public bounds drinkAbsorptionIntervalBounds;
    public bounds controllerDelayBounds;

    // targettedImages
    private Mat prayerIconMat;
    private imageSURF imageSURF = new imageSURF();
    private corners corners;
    private Mat nmzInventoryMat;
    private corners inventoryCorners;
    private Mat[] absorpDoses;

    // mouse controller
    private moveMouse moveMouse = new moveMouse();
    private int xRandomOffset;
    private int yRandomOffset;
    private int[] mouseHoldDown;
    private int[] doubleClickSpeed;
    private msArrays msArrays = new msArrays();
    //private Random random = new Random();
    private utils utils = new utils();
    private mouseClick mouseClick = new mouseClick();
    private int doubleClickOffset;
    private int[] clickingPoint;
    private bounds clicksForPotionDrinking;


    public nmzUtils() throws AWTException{
    }

    /**
     * Load custom config parameters:
     *  - combat: "range" or "melee" - only "range" is supportted atm
     *  - prayerFlickInterval: "25" seconds default
     *  - drinkAbsorptionInterval: "607" seconds default
     *  - drinkSuperRangeInterval: "695" seconds default
     *
     */
    public void loadNMZConfig(){
        Properties prop = new Properties();
        InputStream input = null;

        try{
            input = new FileInputStream("config/config.properties");
            prop.load(input);

            String combat = prop.getProperty("nmz.combat"); // only ranged is supported atm, var not used
            int prayerFlickInterval = Integer.parseInt(prop.getProperty("nmz.prayerFlickInterval"));
            //int drinkAbsorptionInterval = Integer.parseInt(prop.getProperty("nmz.drinkAbsorptionInterval"));
            //int drinkSuperRangeInterval = Integer.parseInt(prop.getProperty("nmz.drinkSuperRangeInterval"));

            prayerFlickTimer = new timer(prayerFlickInterval); // prayerFlick intervals will always be the same
            // generate bounds for absorption drinking intervals, not static intervals
            //drinkAbsorptionTimer = new timer(drinkAbsorptionInterval);
            //drinkSuperRangingTimer = new timer(drinkSuperRangeInterval);

            // load endTimes into a global bound variable
            int endTimeLowerBound = Integer.parseInt(prop.getProperty("nmz.endTimeLowerBound"));
            int endTimeUpperBound = Integer.parseInt(prop.getProperty("nmz.endTimeUpperBound"));
            endTimeBounds = new bounds(endTimeLowerBound, endTimeUpperBound);

            // load targettedImages
            String prayerIconString = prop.getProperty("nmz.prayerIcon");
            targettedImage prayerIcon = new targettedImage(prayerIconString);
            prayerIconMat = imageSURF.bufferedImageToMat(prayerIcon.getTargettedImage());

            // load randomOffsets
            xRandomOffset = Integer.parseInt(prop.getProperty("nmz.xRandomOffset"));
            yRandomOffset = Integer.parseInt(prop.getProperty("nmz.yRandomOffset"));

            // load randomized arrays
            int mouseHoldDownLowerBound = Integer.parseInt(prop.getProperty("nmz.mouseHoldDownLowerBound"));
            int mouseHoldDownUpperBound = Integer.parseInt(prop.getProperty("nmz.mouseHoldDownUpperBound"));
            bounds mouseHoldDownBounds = new bounds(mouseHoldDownLowerBound, mouseHoldDownUpperBound);
            int mouseHoldDownArrayLength = Integer.parseInt(prop.getProperty("nmz.mouseHoldDownArrayLength"));
            mouseHoldDown = new int[mouseHoldDownArrayLength];
            mouseHoldDown = msArrays.generateRandomizedIntArray(mouseHoldDownBounds, mouseHoldDown);

            int doubleClickSpeedLowerBound = Integer.parseInt(prop.getProperty("nmz.doubleClickSpeedLowerBound"));
            int doubleClickSpeedUpperBound = Integer.parseInt(prop.getProperty("nmz.doubleClickSpeedUpperBound"));
            bounds doubleClickSpeedBound = new bounds(doubleClickSpeedLowerBound, doubleClickSpeedUpperBound);
            int doubleClickSpeedArrayLength = Integer.parseInt(prop.getProperty("nmz.doubleClickSpeedArrayLength"));
            doubleClickSpeed = new int[doubleClickSpeedArrayLength]; // double click delay
            doubleClickSpeed = msArrays.generateRandomizedIntArray(doubleClickSpeedBound, doubleClickSpeed);

            // load doubleClickOffset
            doubleClickOffset = Integer.parseInt(prop.getProperty("nmz.doubleClickOffset"));

            // load absorption doses
            String absorpDose1String = prop.getProperty("nmz.absorpDose1");
            String absorpDose2String = prop.getProperty("nmz.absorpDose2");
            String absorpDose3String = prop.getProperty("nmz.absorpDose3");
            String absorpDose4String = prop.getProperty("nmz.absorpDose4");

            targettedImage absorpDose1 = new targettedImage(absorpDose1String);
            targettedImage absorpDose2 = new targettedImage(absorpDose2String);
            targettedImage absorpDose3 = new targettedImage(absorpDose3String);
            targettedImage absorpDose4 = new targettedImage(absorpDose4String);

            Mat absorpDose1Mat = imageSURF.bufferedImageToMat(absorpDose1.getTargettedImage());
            Mat absorpDose2Mat = imageSURF.bufferedImageToMat(absorpDose2.getTargettedImage());
            Mat absorpDose3Mat = imageSURF.bufferedImageToMat(absorpDose3.getTargettedImage());
            Mat absorpDose4Mat = imageSURF.bufferedImageToMat(absorpDose4.getTargettedImage());
            absorpDoses = new Mat[]{absorpDose1Mat, absorpDose2Mat, absorpDose3Mat, absorpDose4Mat};

            // generate potion drinking variables
            int clicksForPotionDrinkingLowerBound = Integer.parseInt(prop.getProperty("nmz.clicksForPotionDrinkingLowerBound"));
            int clicksForPotionDrinkingUpperBound = Integer.parseInt(prop.getProperty("nmz.clicksForPotionDrinkingUpperBound"));
            clicksForPotionDrinking = new bounds(clicksForPotionDrinkingLowerBound, clicksForPotionDrinkingUpperBound);

            // load inventory image - with an inventory full of absorption pots and 1 rock cake
            String nmzInventoryString = prop.getProperty("nmz.inventory");
            targettedImage nmzInventory = new targettedImage(nmzInventoryString);
            nmzInventoryMat = imageSURF.bufferedImageToMat(nmzInventory.getTargettedImage());
            findInventory();

            // bounds for drinking absorption pot intervals
            int drinkAbsorptionIntervalLowerBound = Integer.parseInt(prop.getProperty("nmz.drinkAbsorptionIntervalLowerBound"));
            int drinkAbsorptionIntervalUpperBound = Integer.parseInt(prop.getProperty("nmz.drinkAbsorptionIntervalUpperBound"));
            drinkAbsorptionIntervalBounds = new bounds(drinkAbsorptionIntervalLowerBound, drinkAbsorptionIntervalUpperBound);
            drinkAbsorptionTimer = new timer(utils.randomPickNumberInRange(
                    drinkAbsorptionIntervalBounds.getLowerBound(),
                    drinkAbsorptionIntervalBounds.getUpperBound()
            ));

            // delay before next iteration for controller()
            int controllerDelayLowerBound = Integer.parseInt(prop.getProperty("nmz.controllerDelayLowerBound"));
            int controllerDelayUpperBound = Integer.parseInt(prop.getProperty("nmz.controllerDelayUpperBound"));
            controllerDelayBounds = new bounds(controllerDelayLowerBound, controllerDelayUpperBound);


        }catch(IOException | AWTException e){
            logger.log(Level.SEVERE, "{0}", e.getMessage());
        }finally{
            if(input != null){
                try{
                    input.close();
                }catch(IOException e){
                    logger.log(Level.SEVERE, "{0}", e.getMessage());
                }
            }
        }
    }

    public boolean prayerFlick() throws AWTException, InterruptedException{

        // load prayer icon into bufferedImage, then convert into Mat object
        imageSURF.loadSnapshotIntoMat();
        boolean targettedImageFound = imageSURF.SURF(prayerIconMat, 1, 2, 1, 0.7f, 15);
        if(targettedImageFound){
            // retrieve coordinates
            corners = imageSURF.getFoundImageCorners();
            clickingPoint = corners.calculateCenter();
            moveMouse.randomizedMoveMouse(clickingPoint[0], clickingPoint[1], xRandomOffset, yRandomOffset);

            // generate 2 random numbers - mouse hold down (double click)
            int[] mouseHoldDownValues = utils.randomPick2numbersFromArray(mouseHoldDown);
            // generate 1 random number - doubleClickDelay
            int doubleClickDelay = utils.randomPick1numberFromArray(doubleClickSpeed);

            // doubleClick should have a "slight offset" on each click
            mouseClick.doubleClickWithOffset(mouseHoldDownValues, doubleClickDelay, doubleClickOffset);
            return true;
        }else{
            logger.log(Level.SEVERE, "targettedImage not found");
            return false;
        }
    }

    /**
     * In order to drinkAbsorption(), we need to first find our inventory
     * findInventory() should only be called at the start
     * since our inventory position should never change
     *
     * Currently, we are searching the entire 1920x1080 screenshot for a 40x40 image of a potion
     * OpenCV feature detection algorithms are not able to detect our targettedImage 100% of the time
     * With a bit of tweaking to SURF(), I was able to get up to ~80% successful detection rate
     * This is not good enough since sometimes the detected corners WENT OFF THE SCREEN
     *
     * For larger targettedImages, OpenCV algorithms work fine
     */
    private void findInventory() throws AWTException{
        /**
         * 1. get coordinates of inventory
         * 2. use Robot to generate a screenshot of ONLY the inventory
         * 3. use SURF() to find absorption potions
         */

        imageSURF.loadSnapshotIntoMat();
        boolean targettedImageFound = imageSURF.SURF(nmzInventoryMat, 1, 2, 1, 0.7f, 120);
        if(targettedImageFound){
            inventoryCorners = imageSURF.getFoundImageCorners();
            inventoryCorners.printAllCorners();
            return;
        }

        // inventory not found
        logger.log(Level.SEVERE, "inventory not found");
        System.exit(0);
    }

    public boolean drinkAbsorption() throws AWTException{

        // only perform search on inventory
        imageSURF.loadSnapshotIntoMat(inventoryCorners);
        for(Mat absorpDose: absorpDoses){
            // use templateMatch() instead of SURF for smaller images
            boolean targettedImageFound = imageSURF.templateMatch(absorpDose, 0.988);
            if(targettedImageFound){
                corners = imageSURF.getFoundImageCorners();
                clickingPoint = corners.calculateCenter();

                // get topLeft corner for inventory first
                int[] topLeftInventory = inventoryCorners.getTopLeftCorner();
                moveMouse.randomizedMoveMouse(
                        clickingPoint[0] + topLeftInventory[0],
                        clickingPoint[1] + topLeftInventory[1],
                        xRandomOffset,
                        yRandomOffset
                );

                int numberOfClicks = utils.randomPickNumberInRange(
                        clicksForPotionDrinking.getLowerBound(),
                        clicksForPotionDrinking.getUpperBound()
                );

                logger.log(Level.FINE, "numberOfClicks: {0}", numberOfClicks);
                int[] mouseHoldDownValues = utils.randomPickItemsFromArray(mouseHoldDown, numberOfClicks);
                // we can use doubleClickSpeed[] values - no need to create a new array for spam clicks
                int[] clickDelayValues = utils.randomPickItemsFromArray(doubleClickSpeed, numberOfClicks);
                mouseClick.multiClickWithOffset(mouseHoldDownValues, clickDelayValues, doubleClickOffset);
                return true;
            }
        }
        // not found
        logger.log(Level.SEVERE, "No absorption potions left");
        return false;
    }

    public void drinkSuperRanging(){


    }
}
