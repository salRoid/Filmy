package tech.salroid.filmy.utility

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
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

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun String.toReadableDate(): String {
    if (this.isEmpty()) {
        return this
    }
    val fromDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val toDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return toDateFormat.format(fromDateFormat.parse(this))
}

fun Activity.themeSystemBars(lightTheme: Boolean = false, lightStatusBar: Boolean = false) {
    window.apply {
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        statusBarColor = Color.TRANSPARENT

        if (lightTheme) {
            navigationBarColor =
                ContextCompat.getColor(this@themeSystemBars, R.color.surfaceColorLight)
            var flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
            if (lightStatusBar) {
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            decorView.systemUiVisibility = flags
        }
    }
}