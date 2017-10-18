package imageUtils;

import loggerUtils.loggerUtils;
import mouseUtils.mouseClick;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Load each individual targettedImage into a targettedImage object
 */
public class targettedImage{

    private static Logger logger = Logger.getLogger(targettedImage.class.getName());
    static{
        loggerUtils.setLoggerConfig(logger);
    }

    private BufferedImage targettedImage;
    private String[] imageExtensions = new String[]{"gif", "png", "bmp", "jpg"};

    public BufferedImage getTargettedImage(){
        return targettedImage;
    }

    public targettedImage(String filename){
        loadTargettedImage(filename);
    }

    private void loadTargettedImage(String filename){
        Properties prop = new Properties();
        InputStream input = null;

        try{
            input = new FileInputStream("config/config.properties");
            prop.load(input);
            String picturesDirectoryString = prop.getProperty("picturesDirectory");

            File picturesDirectory = new File(picturesDirectoryString);
            if(picturesDirectory.isDirectory()){

                /**
                 * check if file already has extension
                 *  1. convert filename to lowercase
                 *  2. "pipe" it through imageExtensions[] to see if match
                 *      2.1 if so, do nothing
                 *      2.2 else, return the extension that matches
                 *  3. append extension to string
                 *  4. create a path: "pictures/filename.extension"
                 */

                boolean checkFilenameHasExt = filterFileExtension(filename);
                String absoluteFilename = picturesDirectoryString + "/" + filename;
                if(!checkFilenameHasExt){
                    absoluteFilename = getFilenameWithExt(absoluteFilename);
                }

                File targettedImageFile = new File(absoluteFilename);
                if(targettedImageFile.isFile()){
                    logger.log(Level.FINER, "Writing found image to BufferedImage");
                    targettedImage = ImageIO.read(targettedImageFile);
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

    private String getFilenameWithExt(String absoluteFileName){
        for(String ext: imageExtensions){
            String checkIfFileString = absoluteFileName + "." + ext;
            File checkIfFile = new File(checkIfFileString);
            if(checkIfFile.isFile()){
                logger.log(Level.FINE, "Found file: {0}", checkIfFileString);
                return checkIfFileString;
            }
        }
        logger.log(Level.SEVERE, "Cannot find file: {0}", absoluteFileName);
        System.exit(0); // exit program
        return null;
    }

    private boolean filterFileExtension(String filename){
        for(String ext: imageExtensions){
            if(filename.toLowerCase().endsWith(ext)){
                logger.log(Level.FINEST, "Given filename {0} already has extension", filename);
                return true;
            }
        }
        logger.log(Level.FINER, "  Filename missing extension; searching...");
        return false;
    }
}
