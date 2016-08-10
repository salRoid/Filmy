package tech.salroid.filmy.utils;

/**
 * Created by R Ankit on 07-08-2016.
 */

public class NullChecker
  {
      public static <T> boolean isSettable(T stuff){

          return !(stuff == null || stuff.equals("null"));
      }

}
