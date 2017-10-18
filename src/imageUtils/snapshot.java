package imageUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import loggerUtils.loggerUtils;
import utils.utils;

public class snapshot{

    private static Logger logger = Logger.getLogger(snapshot.class.getName());
    static{
        loggerUtils.setLoggerConfig(logger);
    }

    private Robot robot = new Robot();
    private utils utils = new utils();

    private String picturesDirectoryString = null;
    private File picturesDirectory = null;
    private String[] imageExtensions = new String[]{"gif", "png", "bmp", "jpg"};
    private BufferedImage[] targettedImages = null; // unused
    private BufferedImage snapshot = null;

    public snapshot() throws AWTException{
    }

    public BufferedImage getSnapshot(){
        return snapshot;
    }


    /**
     * Captures current screen - snapshot
     * We search for our targetted images in the snapshot
     */
    public void takeSnapshot(){
        int screenWidth = utils.getScreenWidth();
        int screenHeight = utils.getScreenHeight();
        Rectangle screenDimensions = new Rectangle(0, 0, screenWidth, screenHeight);
        snapshot = robot.createScreenCapture(screenDimensions);
    }

    /**
     * Captures a specific rectangular area of the entire screen
     * Test with displayBufferedImage()
     *
     * @param corners
     */
    public void takeSnapshot(corners corners){
        int[] topLeftCorner = corners.getTopLeftCorner();
        int[] bottomRightCorner = corners.getBottomRightCorner();

        Rectangle targettedDimensions = new Rectangle(
                topLeftCorner[0],
                topLeftCorner[1],
                bottomRightCorner[0] - topLeftCorner[0],
                bottomRightCorner[1] - topLeftCorner[1]
        );
        snapshot = robot.createScreenCapture(targettedDimensions);
        //displayBufferedImage(snapshot); // dont think there is an issue here
    }


    /**
     * Renders + Displays BufferedImage
     * Used for testing
     *
     * @param image
     */
    public void displayBufferedImage(BufferedImage image){
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * User can specify their own pictures directory
     * Initializes targettedImages
     *
     * UNUSED
     */
    public void loadBufferedImages(){

        Properties prop = new Properties();
        InputStream input = null;

        try{
            input = new FileInputStream("config/config.properties");
            prop.load(input);
            picturesDirectoryString = prop.getProperty("picturesDirectory");

            picturesDirectory = new File(picturesDirectoryString);
            if(picturesDirectory.isDirectory()){

                // filter out only images in directory
                // might need a null check here
                File[] filesArray = picturesDirectory.listFiles();
                filesArray = filterFileArray(filesArray);
                filesArray = removeNullValuesFromArray(filesArray);

                printFiles(filesArray);

                targettedImages = new BufferedImage[filesArray.length];

                for(int i = 0; i < filesArray.length; i++){
                    BufferedImage image = ImageIO.read(filesArray[i]);
                    targettedImages[i] = image;
                }
            }
        }catch(IOException e){
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

    /**
     * Filters all non image files - based on imageExtensions
     *
     * @param files
     * @return
     */
    private File[] filterFileArray(File[] files){

        for(int i = 0; i < files.length; i++){
            boolean extensionFound = false;
            for(String ext: imageExtensions){
                if(files[i].getName().toLowerCase().endsWith("." + ext)){
                    extensionFound = true;
                }
            }
            if(!extensionFound){
                files[i] = null;
            }
        }
        return files;
    }

    /**
     * Converts File[] into an ArrayList of File
     * Then filter out all null values
     *
     * @param files
     * @return
     */
    private File[] removeNullValuesFromArray(File[] files){
        ArrayList<File> fileList = new ArrayList<>(Arrays.asList(files));
        fileList.removeAll(Collections.singleton(null));
        return fileList.toArray(new File[fileList.size()]);
    }

    /**
     * Print the name of each File in a File[]
     * Used for testing
     *
     * @param files
     */
    public void printFiles(File[] files){
        for(File file: files){
            logger.log(Level.FINER, "  {0}", file.getName());
        }
    }
}
