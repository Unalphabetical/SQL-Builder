import java.util.Arrays;

public class Utilities {

    /**
     * Returns the index of the value in the array
     *
     * @param array The array
     * @param value The value 
     * @return the index of the value
     */
    public static int findIndex(String[] array, String value) {
        return Arrays.asList(array).indexOf(value);
    }

}
