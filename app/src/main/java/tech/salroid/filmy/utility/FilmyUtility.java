package tech.salroid.filmy.utility;

import android.content.Context;
import android.content.res.Resources;

public class FilmyUtility {

    public static int getStatusBarHeight(Context context) {
        int height;

        Resources myResources = context.getResources();
        int idStatusBarHeight = myResources.getIdentifier(
                "status_bar_height", "dimen", "android");
        if (idStatusBarHeight > 0)
            height = context.getResources().getDimensionPixelSize(idStatusBarHeight);
        else
            height = 0;

        return height;
    }
}
