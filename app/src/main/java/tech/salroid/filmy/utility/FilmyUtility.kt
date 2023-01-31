package tech.salroid.filmy.utility

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.TypedArray
import androidx.recyclerview.widget.GridLayoutManager
import tech.salroid.filmy.R
import android.R as AndroidR

object FilmyUtility {

    @Suppress("NOTHING_TO_INLINE")
    inline fun <T> unsafeLazy(noinline initializer: () -> T): Lazy<T> =
        lazy(LazyThreadSafetyMode.NONE, initializer)

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

    @SuppressLint("InternalInsetResource")
    fun getNavigationBarHeight(context: Context): Int {
        val resources = context.applicationContext.resources
        val idNavigationBarHeight: Int =
            resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (idNavigationBarHeight > 0) {
            context.resources.getDimensionPixelSize(idNavigationBarHeight)
        } else {
            context.resources.getDimensionPixelSize(R.dimen.filmy42dp)
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
            Configuration.ORIENTATION_PORTRAIT -> 3
            else -> 6
        }
        return GridLayoutManager(
            context, spanCount, GridLayoutManager.VERTICAL, false
        )
    }

    fun startSharingIntent(context: Context) {
        val appShareDetails = context.resources.getString(R.string.app_share_link)
        val myIntent = Intent(Intent.ACTION_SEND)
        myIntent.type = "text/plain"
        myIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Check out this awesome movie app.\n*filmy*\n$appShareDetails"
        )
        context.startActivity(Intent.createChooser(myIntent, "Share with"))
    }
}