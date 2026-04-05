package tech.salroid.filmy.ui.widget

import android.content.Context
import androidx.glance.appwidget.updateAll

object WidgetRefresher {
    suspend fun refresh(context: Context) {
        WatchlistWidget().updateAll(context)
    }
}
