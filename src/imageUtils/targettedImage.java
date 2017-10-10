package imageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Load each individual targettedImage into a targettedImage object
 */
public class targettedImage{

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
                 * todo: check if file already has extension
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
                    System.out.println("  writing to BufferedImage");
                    targettedImage = ImageIO.read(targettedImageFile);
                }
            }
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

    private String getFilenameWithExt(String absoluteFileName){
        System.out.println("getFilenameWithExt()");
        for(String ext: imageExtensions){
            String checkIfFileString = absoluteFileName + "." + ext;
            File checkIfFile = new File(checkIfFileString);
            if(checkIfFile.isFile()){
                System.out.println("  found file: " + checkIfFileString);
                return checkIfFileString;
            }
        }
        System.out.println("  Cannot find file");
        System.exit(0); // exit program
        return null;
    }

    private boolean filterFileExtension(String filename){
        System.out.println("filterFileExtension()");
        for(String ext: imageExtensions){
            if(filename.toLowerCase().endsWith(ext)){
                System.out.println("  filename already has extension");
                return true;
            }
        }
        System.out.println("  filename missing extension - searching...");
        return false;
    }
}
