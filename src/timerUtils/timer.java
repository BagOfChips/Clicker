package timerUtils;

public class timer{

    private long startTime;
    private int runningTime;
    private int reloadTime;

    public timer(){
        startTime = System.currentTimeMillis();
    }

    public timer(int reloadTime){
        startTime = System.currentTimeMillis();
        this.reloadTime = reloadTime;
    }

    public void resetRunningTime(){
        runningTime = 0;
    }

    public void incrementRunningTime(int seconds){
        runningTime += seconds;
    }

    public int getRunningTime(){
        return runningTime;
    }

    public int getReloadTime(){
        return reloadTime;
    }

    public void setReloadTime(int seconds){
        reloadTime = seconds;
    }

    private int convertToSeconds(long ms){
        return (int) ms / 1000;
    }

    private int getElapsedSeconds(){
        return convertToSeconds(System.currentTimeMillis() - startTime);
    }

    public boolean checkIfIntervalPassed(int randomSeconds){
        return getElapsedSeconds() > randomSeconds;
    }

    public void sleep(int secondsToSleep) throws InterruptedException{
        Thread.sleep((long) secondsToSleep * 1000);
    }

}
