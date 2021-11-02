package tech.salroid.filmy.utility

import android.content.Context

object FilmyUtility {

    fun getStatusBarHeight(context: Context): Int {
        val height: Int
        val myResources = context.resources
        val idStatusBarHeight = myResources.getIdentifier(
            "status_bar_height", "dimen", "android"
        )
        height =
            if (idStatusBarHeight > 0) context.resources.getDimensionPixelSize(idStatusBarHeight) else 0
        return height
    }
}