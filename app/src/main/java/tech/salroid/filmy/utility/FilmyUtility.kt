package tech.salroid.filmy.utility

import android.R as AndroidR
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray

object FilmyUtility {

    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    fun getStatusBarHeight(context: Context): Int {
        val myResources = context.resources
        val idStatusBarHeight = myResources.getIdentifier(
            "status_bar_height",
            "dimen",
            "android"
        )
        return if (idStatusBarHeight > 0) {
            context.resources.getDimensionPixelSize(idStatusBarHeight)
        } else {
            0
        }
    }

    fun getToolBarHeight(context: Context): Int {
        val attrs = intArrayOf(AndroidR.attr.actionBarSize)
        val ta: TypedArray = context.obtainStyledAttributes(attrs)
        val toolBarHeight = ta.getDimensionPixelSize(0, -1)
        ta.recycle()
        return toolBarHeight
    }
}