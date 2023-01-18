package tech.salroid.filmy.utility

import android.R as AndroidR
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.content.res.TypedArray
import androidx.recyclerview.widget.GridLayoutManager

object FilmyUtility {

    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    fun getStatusBarHeight(context: Context): Int {
        val myResources = context.resources
        val idStatusBarHeight = myResources.getIdentifier(
            "status_bar_height", "dimen", "android"
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

    fun getGridLayoutManager(context: Context): GridLayoutManager {
        val spanCount = when (context.resources?.configuration?.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> 2
            else -> 1
        }
        return GridLayoutManager(
            context, spanCount, GridLayoutManager.HORIZONTAL, false
        )
    }
}