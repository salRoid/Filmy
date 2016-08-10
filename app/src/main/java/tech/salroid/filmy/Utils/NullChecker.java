package tech.salroid.filmy.Utils;

public class NullChecker {

    public static <T> boolean isSettable(T stuff) {

        return !(stuff == null || stuff.equals("null"));
    }

}
