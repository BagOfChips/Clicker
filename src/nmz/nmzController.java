package nmz;

import timerUtils.timer;

import java.awt.*;

public class nmzController{

    private timer controllerTimer = new timer();
    private nmzUtils nmzUtils = new nmzUtils();

    public nmzController() throws AWTException{}

    /**
     * todo: generate a randomSecondsIntervalCheck[] - look at msArrays
     *  - 200 values
     *  - shuffle after use
     *  - re use
     *
     * populate with values between config.properties bounds
     *  - default (lower): 7
     *  - default (upper): 18
     *
     * after wait for seconds // todo: implement sleep function in timerUtils
     *  - keep track of "runnning" interval time // todo: implement runningTime and reloadTime variables in timerUtils
     *  - update runningTime
     *  - do action if runningTime >= reloadTime
     *  - reset runningTime
     *
     * keep running to end - see below todo:
     */
    public void controller(){
        nmzUtils.loadNMZConfig();




    }

    /**
     * todo: timeout options?
     *  1. if cannot find targettedImage after 3 tries
     *  2. stop program after 4hrs 15mins - 5hrs 15mins
     */



}
