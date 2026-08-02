package tech.salroid.filmy.utility

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.res.Configuration
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.core.view.WindowCompat
import com.google.android.material.elevation.SurfaceColors
import com.google.android.material.snackbar.Snackbar
import tech.salroid.filmy.R
import java.text.SimpleDateFormat
import java.util.*

fun View.showSnackBar(message: String, positive: Boolean = true) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).run {
        setBackgroundTint(
            if (positive) ContextCompat.getColor(context, R.color.colorMore) else
                ContextCompat.getColor(context, R.color.tomatoRed)
        )
        setTextColor(ContextCompat.getColor(context, R.color.white))
        show()
    }
}

fun String.toReadableDate(): String {
    if (this.isEmpty()) return this

    val fromDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val toDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val date = fromDateFormat.parse(this)
    return date?.let { toDateFormat.format(it) } ?: this
}

fun String.parseHtml(): String {
    return if (this.isEmpty()) this else {
        HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY).toString().trim()
    }
}

fun Activity.themeSystemBars(
    lightStatusBar: Boolean = false,
    navigationColorAsStatus: Boolean = true,
    isFullScreen: Boolean = false,
    transparentStatus: Boolean = false,
    surfaceStatus: Boolean = false
) {
    val lightTheme = !isDarkThemeActivated()

    window.apply {
        val colorStatus = SurfaceColors.SURFACE_0.getColor(this@themeSystemBars)
        val colorNavigation = SurfaceColors.SURFACE_2.getColor(this@themeSystemBars)
        val colorStatusSurface = SurfaceColors.SURFACE_3.getColor(this@themeSystemBars)

        window.statusBarColor =
            if (transparentStatus) Color.TRANSPARENT else if (surfaceStatus) colorStatusSurface else colorStatus
        window.navigationBarColor = if (navigationColorAsStatus) colorStatus else colorNavigation

        if (isFullScreen) WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        if (lightTheme) {
            windowInsetsController.isAppearanceLightNavigationBars = true
            windowInsetsController.isAppearanceLightStatusBars = lightStatusBar
        } else {
            windowInsetsController.isAppearanceLightNavigationBars = false
            windowInsetsController.isAppearanceLightStatusBars = false
        }
    }
}

fun Context.isDarkThemeActivated(): Boolean =
    (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

fun Context.openYoutubeTrailer(source: String?) {
    source?.let {
        val intent = Intent(
            ACTION_VIEW,
            "vnd.youtube:$it".toUri()
        )
        if (intent.resolveActivity(packageManager) == null) {
            startActivity(
                Intent(
                    ACTION_VIEW,
                    "https://www.youtube.com/watch?v=$it".toUri()
                )
            )
        } else {
            startActivity(intent)
        }
    }
}

fun Context.shareMedia(title: String, tagline: String?, imdbId: String?, isTvShow: Boolean) {
    val imdbPrefix = getString(R.string.imdb_link_prefix)
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        val prefix = if (!isTvShow) imdbPrefix else ""
        val link = if (!isTvShow && imdbId != null) "\n$prefix$imdbId" else ""
        putExtra(
            Intent.EXTRA_TEXT,
            "*$title*\n${tagline ?: ""}$link\n"
        )
    }
    startActivity(Intent.createChooser(shareIntent, "Share with"))
}
