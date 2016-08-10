package tech.salroid.filmy.utility;

public class NullChecker {

    public static <T> boolean isSettable(T stuff) {

        return !(stuff == null || stuff.equals("null"));
    }

}
