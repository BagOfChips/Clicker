import java.util.Random;

/**
 * utils: utility functions
 */
public class utils{

    // generics
    public < E > void shuffleArray(E[] array){
        int randomIndex;
        Random random = new Random();

        for(int i = 0; i < array.length; i++){
            randomIndex = random.nextInt(array.length);
            swap(array, randomIndex, i);
        }
    }

    public < E > void swap(E[] array, int i, int j){
        E temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    // handles int
    public void shuffleArray(int[] array){
        int randomIndex;
        Random random = new Random();

        for(int i = 0; i < array.length; i++){
            randomIndex = random.nextInt(array.length);
            swap(array, randomIndex, i);
        }
    }

    public void swap(int[] array, int i, int j){
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public void printArray(int[] array){
        System.out.println();
        System.out.println("Printing Array: ");
        for(int i: array){
            System.out.println(i);
        }

    }
}
