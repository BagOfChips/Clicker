import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

/**
 * msArrays: defines clicking intervals - all randomized
 */
public class msArrays{
    private int[] msFast;
    private int[] msSlow;
    private int[] mouseHoldDown;

    private bounds fastClickBounds;
    private bounds slowClickBounds;
    private bounds mouseDownBounds;
    private bounds fastClickBoundsLimit;

    private bounds fastClicksInterval;
    private bounds slowClicksInterval;

    public msArrays(){

        Properties prop = new Properties();
        InputStream input = null;

        try{
            input = new FileInputStream("config/config.properties");
            prop.load(input);

            int fastClickLowerBound
                    = Integer.parseInt(prop.getProperty("fastClickLowerBound"));
            int fastClickUpperBound
                    = Integer.parseInt(prop.getProperty("fastClickUpperBound"));

            int slowClickLowerBound
                    = Integer.parseInt(prop.getProperty("slowClickLowerBound"));
            int slowClickUpperBound
                    = Integer.parseInt(prop.getProperty("slowClickUpperBound"));

            int mouseDownLowerBound
                    = Integer.parseInt(prop.getProperty("mouseDownLowerBound"));
            int mouseDownUpperBound
                    = Integer.parseInt(prop.getProperty("mouseDownUpperBound"));

            int fastClicksIntervalLowerBound
                    = Integer.parseInt(prop.getProperty("fastClicksIntervalLowerBound"));
            int fastClicksIntervalUpperBound
                    = Integer.parseInt(prop.getProperty("fastClicksIntervalUpperBound"));

            int slowClicksIntervalLowerBound
                    = Integer.parseInt(prop.getProperty("slowClicksIntervalLowerBound"));
            int slowClicksIntervalUpperBound
                    = Integer.parseInt(prop.getProperty("slowClicksIntervalUpperBound"));

            int msFastArrayLength = Integer.parseInt(prop.getProperty("msFastArrayLength"));
            int msSlowArrayLength = Integer.parseInt(prop.getProperty("msSlowArrayLength"));
            int mouseHoldDownArrayLength
                    = Integer.parseInt(prop.getProperty("mouseHoldDownArrayLength"));

            fastClickBounds = new bounds(fastClickLowerBound, fastClickUpperBound);
            slowClickBounds = new bounds(slowClickLowerBound, slowClickUpperBound);
            mouseDownBounds = new bounds(mouseDownLowerBound, mouseDownUpperBound);
            fastClickBoundsLimit = new bounds(
                    fastClickBounds.getLowerBound() / 4,
                    fastClickBounds.getUpperBound() * 4
            );

            fastClicksInterval
                    = new bounds(fastClicksIntervalLowerBound, fastClicksIntervalUpperBound);
            slowClicksInterval
                    = new bounds(slowClicksIntervalLowerBound, slowClicksIntervalUpperBound);

            msFast = new int[msFastArrayLength];
            msSlow = new int[msSlowArrayLength];
            mouseHoldDown = new int[mouseHoldDownArrayLength];
            generateRandomizedmsArrays();

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

    private void generateRandomizedmsArrays(){
        generateRandomizedIntArray(
                fastClickBounds.getLowerBound(),
                fastClickBounds.getUpperBound(),
                msFast
        );
        generateRandomizedSpikedArray(
                fastClickBounds.getLowerBound(),
                fastClickBounds.getUpperBound(),
                msFast,
                fastClickBoundsLimit
        );
        generateRandomizedIntArray(
                slowClickBounds.getLowerBound(),
                slowClickBounds.getUpperBound(),
                msSlow
        );
        generateRandomizedIntArray(
                mouseDownBounds.getLowerBound(),
                mouseDownBounds.getUpperBound(),
                mouseHoldDown
        );
    }

    // for msFast usage
    private void generateRandomizedSpikedArray(
            int lowerBound, int upperBound, int[] array, bounds boundsLimit){

        Random random = new Random();

        // todo: there should be checks to make sure the altered bounds are within correct range
        lowerBound = (int) (lowerBound * 1.01);
        upperBound = (int) (upperBound * 0.99);
        for(int i = 0; i < array.length; i++){
            if(array[i] <= lowerBound){

                array[i] = random.nextInt(
                        (lowerBound - boundsLimit.getLowerBound()) + 1)
                        + boundsLimit.getLowerBound();

            }else if(array[i] >= upperBound){

                array[i] = random.nextInt(
                        (boundsLimit.getUpperBound() - upperBound) + 1)
                        + upperBound;

            }
        }
    }

    private void generateRandomizedIntArray(
            int lowerBound, int upperBound, int[] array){

        Random random = new Random();
        for(int i = 0; i < array.length; i++){
            array[i] = random.nextInt(
                    (upperBound - lowerBound) + 1) + lowerBound;
        }
    }

    public void printmsArrays(){
        System.out.println("Fast ms");
        System.out.println("Array length: " + msFast.length);
        for(int i: msFast){
            System.out.println(i);
        }

        System.out.println();
        System.out.println("Slow ms");
        System.out.println("Array length: " + msSlow.length);
        for(int i: msSlow){
            System.out.println(i);
        }

        System.out.println();
        System.out.println("Mouse hold down");
        System.out.println("Array length: " + mouseHoldDown.length);
        for(int i: mouseHoldDown){
            System.out.println(i);
        }
    }

    public int[] getmsFast(){
        return msFast;
    }

    public int[] getMsSlow(){
        return msSlow;
    }

    public int[] getMouseHoldDown(){
        return mouseHoldDown;
    }

    public bounds getFastClickBounds(){
        return fastClickBounds;
    }

    public bounds getSlowClickBounds(){
        return slowClickBounds;
    }

    public bounds getMouseDownBounds(){
        return mouseDownBounds;
    }

    public bounds getFastClickBoundsLimit(){
        return fastClickBoundsLimit;
    }

    public bounds getFastClicksInterval(){
        return fastClicksInterval;
    }

    public bounds getSlowClicksInterval(){
        return slowClicksInterval;
    }
}
