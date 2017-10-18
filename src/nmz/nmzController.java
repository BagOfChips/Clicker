package nmz;

import timerUtils.timer;
import utils.utils;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import loggerUtils.loggerUtils;

public class nmzController{

    private static Logger logger = Logger.getLogger(nmzController.class.getName());
    static{
        logger = loggerUtils.setLoggerConfig(logger);
    }

    private timer controllerTimer = new timer();
    private nmzUtils nmzUtils = new nmzUtils();
    private utils utils = new utils();

    public nmzController() throws AWTException{
    }

    public runResult run() throws InterruptedException, AWTException{

        nmzUtils.loadNMZConfig();

        setEndTime();
        nmzUtils.drinkAbsorptionTimer.resetRunningTime();
        nmzUtils.prayerFlickTimer.resetRunningTime();
        controllerTimer.resetRunningTime();
        setAbsorptionReload();

        // hard code limits
        nmzChecks prayerFlickChecks = new nmzChecks(2);
        nmzChecks absorptionChecks = new nmzChecks(10);
        while(controllerTimer.getRunningTime() < controllerTimer.getReloadTime()
                && prayerFlickChecks.getCurrentChecks() < prayerFlickChecks.getChecksLimit()
                && absorptionChecks.getCurrentChecks() < absorptionChecks.getChecksLimit()){

            int iterationStart = (int) System.currentTimeMillis();

            // prayer flick
            if(nmzUtils.prayerFlickTimer.getRunningTime() >= nmzUtils.prayerFlickTimer.getReloadTime()){
                if(!nmzUtils.prayerFlick()){
                    prayerFlickChecks.incrementCurrentChecks();
                }
                nmzUtils.prayerFlickTimer.resetRunningTime();
            }

            // drink absorption
            if(nmzUtils.drinkAbsorptionTimer.getRunningTime() >= nmzUtils.drinkAbsorptionTimer.getReloadTime()){
                if(!nmzUtils.drinkAbsorption()){
                    absorptionChecks.incrementCurrentChecks();
                }
                nmzUtils.drinkAbsorptionTimer.resetRunningTime();
                setAbsorptionReload();
            }

            // random delay before next iteration
            delayBeforeNextIteration();

            int iterationEnd = (int) System.currentTimeMillis();
            int iterationElapsedTime = iterationEnd - iterationStart;
            incrementRuntimes(iterationElapsedTime);
        }

        /**
         * out of main loop now, 1 of 4 three scenarios
         *  1. 4 - 6 hours has passed
         *  2. ran out of absorption pots
         *  3. can no longer find prayer icon
         *  4. dont know
         */
        return finishedResult(prayerFlickChecks, absorptionChecks);

    }

    private runResult finishedResult(nmzChecks prayerFlicksCheck, nmzChecks absorptionChecks){
        if(prayerFlicksCheck.getCurrentChecks() >= prayerFlicksCheck.getChecksLimit()){
            return runResult.CANNOT_FIND_PRAYER_ICON;
        }else if(absorptionChecks.getCurrentChecks() >= absorptionChecks.getChecksLimit()){
            return runResult.NO_ABSORPTIONS_LEFT;
        }else if(controllerTimer.getRunningTime() >= controllerTimer.getReloadTime()){
            return runResult.TIMEOUT;
        }else{
            return runResult.UNKNOWN_END_RESULT;
        }
    }

    private void incrementRuntimes(int ms){
        nmzUtils.prayerFlickTimer.incrementRunningTime(ms);
        nmzUtils.drinkAbsorptionTimer.incrementRunningTime(ms);
        controllerTimer.incrementRunningTime(ms);
        logTimes();
    }

    private void logTimes(){
        logger.log(Level.FINE, "  Last prayer flick: {0}", nmzUtils.prayerFlickTimer.getRunningTime());
        logger.log(Level.FINE, "  Last absorption sip: {0}", nmzUtils.drinkAbsorptionTimer.getRunningTime());
        logger.log(Level.FINE, "  Total run time: {0}", controllerTimer.getRunningTime());
    }

    private void delayBeforeNextIteration() throws InterruptedException{
        int randomDelay = utils.randomPickNumberInRange(
                nmzUtils.controllerDelayBounds.getLowerBound(),
                nmzUtils.controllerDelayBounds.getUpperBound()
        );
        controllerTimer.sleep(randomDelay);
    }

    private void setEndTime(){
        int endTime = utils.randomPickNumberInRange(
                nmzUtils.endTimeBounds.getLowerBound(),
                nmzUtils.endTimeBounds.getUpperBound()
        );
        controllerTimer.setReloadTime(endTime);
    }


    private void setAbsorptionReload(){
        nmzUtils.drinkAbsorptionTimer.setReloadTime(
                utils.randomPickNumberInRange(
                        nmzUtils.drinkAbsorptionIntervalBounds.getLowerBound(),
                        nmzUtils.drinkAbsorptionIntervalBounds.getUpperBound())
        );
    }
}
