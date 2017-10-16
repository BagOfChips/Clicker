package nmz;

import timerUtils.timer;
import utils.utils;
import java.awt.*;

public class nmzController{

    private timer controllerTimer = new timer();
    private nmzUtils nmzUtils = new nmzUtils();
    private utils utils = new utils();

    public nmzController() throws AWTException{}

    public runResult run() throws InterruptedException, AWTException{
        System.out.println("run()");
        nmzUtils.loadNMZConfig();

        setEndTime();
        nmzUtils.drinkAbsorptionTimer.resetRunningTime();
        nmzUtils.prayerFlickTimer.resetRunningTime();
        controllerTimer.resetRunningTime();
        setAbsorptionReload();

        // hard code limits
        nmzChecks prayerFlickChecks = new nmzChecks(2);
        nmzChecks absorptionChecks = new nmzChecks(50);
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
         * out of main loop now, 1 of 3 three scenarios
         *  1. 4 - 6 hours has passed
         *  2. ran out of absorption pots
         *  3. can no longer find prayer icon
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
        System.out.println("logTimes()");
        System.out.println("  prayerFlick running time: " + nmzUtils.prayerFlickTimer.getRunningTime());
        System.out.println("  absorption running time: " + nmzUtils.drinkAbsorptionTimer.getRunningTime());
        System.out.println("  total (controller) running time: " + controllerTimer.getRunningTime());

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
