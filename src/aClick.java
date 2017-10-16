import java.awt.Robot;
import java.awt.AWTException;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

/**
 * aClick: main autoclicker class
 *
public class aClick{

    private msArrays msArrays = new msArrays();
    private int[] msFast = msArrays.getmsFast();
    private int[] msSlow = msArrays.getMsSlow();
    private int[] mouseHoldDown = msArrays.getMouseHoldDown();
    private bounds fastClickBounds = msArrays.getFastClickBounds();
    private bounds slowClickBounds = msArrays.getSlowClickBounds();
    private bounds fastClickBoundsLimit = msArrays.getFastClickBoundsLimit();
    private bounds fastClicksInterval = msArrays.getFastClicksInterval();
    private bounds slowClicksInterval = msArrays.getSlowClicksInterval();

    private utils utils = new utils();

    // number of clicks to do before switching
    private int numFastClicksInterval;
    private int numLongClicksInterval;

    private Robot clicker = new Robot();

    // mousePointer - controlling random mouse movements
    private mousePointer mousePointer = new mousePointer();
    private coordinates[] coordinates = mousePointer.getCoordinatesArray();

    public aClick() throws AWTException{}

    // todo: clean up this section - split into functions / order events neatly
    public void run() throws InterruptedException{
        while(true){
            numFastClicksInterval = generateClicksInterval(fastClicksInterval);
            int fastClicks = 0;
            int j = 0;  // mouseHoldDown iterator
            int k = 0;  // coordinates iterator
            for(int i = 0;  // msFast iterator
                i < msFast.length && fastClicks <= numFastClicksInterval;
                i++){

                fastClicks++;

                click(mouseHoldDown[j]);
                j++;

                // after clicking move mouse slightly
                mousePointer.moveMouseWithinRadius(coordinates[k]);
                k++;
                if(k >= coordinates.length){
                    k = 0;
                    utils.shuffleArray(coordinates);
                }

                if(j >= mouseHoldDown.length){
                    j = 0;
                    utils.shuffleArray(mouseHoldDown);
                }

                Thread.sleep(msFast[i]);

                if(i == msFast.length - 1){
                    i = 0;
                    utils.shuffleArray(msFast);
                }
            }

            numLongClicksInterval = generateClicksInterval(slowClicksInterval);
            int slowClicks = 0;
            for(int i = 0;
                i < msSlow.length && slowClicks <= numLongClicksInterval;
                i++){

                slowClicks++;

                click(mouseHoldDown[j]);
                j++;

                // after clicking move mouse slightly
                mousePointer.moveMouseWithinRadius(coordinates[k]);
                k++;
                if(k >= coordinates.length){
                    k = 0;
                    utils.shuffleArray(coordinates);
                }

                if(j >= mouseHoldDown.length){
                    j = 0;
                    utils.shuffleArray(mouseHoldDown);
                }

                Thread.sleep(msSlow[i]);

                if(i == msSlow.length - 1){
                    i = 0;
                    utils.shuffleArray(msSlow);
                }
            }
        }
    }

    public void click(int randomHold){
        clicker.delay(randomHold);
        clicker.mousePress(MouseEvent.BUTTON1_DOWN_MASK);
        clicker.delay(randomHold);
        clicker.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK);
    }

    private int generateClicksInterval(bounds bound){
        Random random = new Random();
        int interval = random.nextInt(
                (bound.getUpperBound() - bound.getLowerBound()) + 1) + bound.getLowerBound();
        return interval;
    }

    // todo: rename to alch()
    // todo: create separate method for nmz support
    public void runDebug() throws InterruptedException{
        int l = 0;
        while(l < 12){
            long startTimeFastClicksInterval = System.currentTimeMillis();

            numFastClicksInterval = generateClicksInterval(fastClicksInterval);
            int fastClicks = 0;
            int j = 0;
            int k = 0;
            for(int i = 0;
                i < msFast.length && fastClicks <= numFastClicksInterval;
                i++){



                click(mouseHoldDown[j]);
                j++;

                // after clicking move mouse slightly
                mousePointer.moveMouseWithinRadius(coordinates[k]);
                k++;
                if(k >= coordinates.length){
                    k = 0;
                    mousePointer.generateRandomizedCoordinatesArray(mousePointer.generateclicksIntervalAtPoint());
                    //utils.shuffleArray(coordinates);
                }

                if(j >= mouseHoldDown.length){
                    j = 0;
                    utils.shuffleArray(mouseHoldDown);
                }

                // todo: double click - every 30 seconds~ish
                if(fastClicks % 2 == 0 || fastClicks == 0){
                    Thread.sleep(msFast[i] / 4);
                }else{
                    Thread.sleep(msFast[i] * 8);
                }
                //Thread.sleep(msFast[i]);

                if(i == msFast.length - 1){
                    i = 0;
                    utils.shuffleArray(msFast);
                }

                fastClicks++;
            }

            long endTimeFastClicksInterval
                    = System.currentTimeMillis() - startTimeFastClicksInterval;
            System.out.println("Fast Clicks: " + fastClicks);
            System.out.println("Time: " + endTimeFastClicksInterval);

            long startTimeSlowClicksInterval = System.currentTimeMillis();

            numLongClicksInterval = generateClicksInterval(slowClicksInterval);
            int slowClicks = 0;
            for(int i = 0;
                i < msSlow.length && slowClicks <= numLongClicksInterval;
                i++){

                slowClicks++;

                click(mouseHoldDown[j]);
                j++;

                // after clicking move mouse slightly
                // todo: this looks really jittery, even at radius=1
                mousePointer.moveMouseWithinRadius(coordinates[k]);
                k++;
                if(k >= coordinates.length){
                    k = 0;
                    mousePointer.generateRandomizedCoordinatesArray(mousePointer.generateclicksIntervalAtPoint());
                    //utils.shuffleArray(coordinates);
                }

                if(j >= mouseHoldDown.length){
                    j = 0;
                    utils.shuffleArray(mouseHoldDown);
                }

                Thread.sleep(msSlow[i]);

                if(i == msSlow.length - 1){
                    i = 0;
                    utils.shuffleArray(msSlow);
                }
            }

            System.out.println("Slow Clicks: " + slowClicks);
            long endTimeSlowClicksInterval
                    = System.currentTimeMillis() - startTimeSlowClicksInterval;
            System.out.println("Time: " + endTimeSlowClicksInterval);

            l++;
        }
    }
}
*/