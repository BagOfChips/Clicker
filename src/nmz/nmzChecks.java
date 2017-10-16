package nmz;


public class nmzChecks{

    private int currentChecks = 0;
    private int checksLimit;

    public nmzChecks(int checksLimit){
        this.checksLimit = checksLimit;
    }

    public int getCurrentChecks(){
        return currentChecks;
    }

    public void incrementCurrentChecks(){
        currentChecks++;
    }

    public int getChecksLimit(){
        return checksLimit;
    }

}
