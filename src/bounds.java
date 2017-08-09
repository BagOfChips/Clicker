/**
 * bounds: define the upper and lower bound for random number generator
 */
public class bounds{

    private int upperBound;
    private int lowerBound;

    public bounds(int lowerBound, int upperBound){
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }

    public int getUpperBound(){
        return upperBound;
    }

    public int getLowerBound(){
        return lowerBound;
    }

    public void setUpperBound(int upperBound){
        this.upperBound = upperBound;
    }

    public void setLowerBound(int lowerBound){
        this.lowerBound = lowerBound;
    }
}
