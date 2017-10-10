import imageUtils.*;
import imageUtils.snapshot;
import mouseUtils.moveMouse;
import nmz.nmzUtils;

import java.awt.AWTException;
import java.awt.image.BufferedImage;

public class assignment_01main{

    public static void main(String[] args) throws AWTException, InterruptedException{

        // - alching -
        //Thread.sleep(3000);
        //aClick aClick = new aClick();
        //aClick.runDebug();

        // - nmz - in progress
        //snapshot snapshot = new snapshot();
        //snapshot.loadBufferedImages();
        //BufferedImage image = snapshot.takeSnapshot();
        //snapshot.displayBufferedImage(image);

        // testing imageSURF
        //imageSURF imageSURF = new imageSURF();
        //imageSURF.loadIntoMats();
        //imageSURF.SURF();

        // testing moveMouse
        //moveMouse moveMouse = new moveMouse();
        //moveMouse.randomizedMoveMouse(2, 1920/2, 1080/12, 0, 0);

        // testing targettedImage
        //targettedImage targettedImage = new targettedImage();
        //targettedImage.loadTargettedImage("test_targettedImage_02");
        //snapshot display = new snapshot();
        //display.displayBufferedImage(targettedImage.getTargettedImage());

        // testing prayerFlick()
        //nmzUtils nmzUtils = new nmzUtils();
        //nmzUtils.loadNMZConfig();
        //nmzUtils.prayerFlick();

        // testing drinkAbsorption()
        //nmzUtils nmzUtils = new nmzUtils();
        //nmzUtils.loadNMZConfig();
        //nmzUtils.drinkAbsorption();

        // testing findInventory()
        //nmzUtils nmzUtils = new nmzUtils();
        //nmzUtils.loadNMZConfig();
        //imageUtils.corners corners = nmzUtils.findInventory();
        //imageUtils.snapshot snapshot = new snapshot();
        //snapshot.takeSnapshot(corners);

        // testing drinkAbsorption()
        //nmzUtils nmzUtils = new nmzUtils();
        //nmzUtils.loadNMZConfig();
        //nmzUtils.drinkAbsorption();
        //nmzUtils.prayerFlick();

        // testing templateMatch()
        nmzUtils nmzUtils = new nmzUtils();
        nmzUtils.loadNMZConfig();
        nmzUtils.drinkAbsorption();

    }
}


