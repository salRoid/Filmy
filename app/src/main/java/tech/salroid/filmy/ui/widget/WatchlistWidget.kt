package tech.salroid.filmy.ui.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.action.Action
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.appwidget.cornerRadius
import androidx.glance.material3.ColorProviders
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.ui.home.MoviesRepository
import tech.salroid.filmy.ui.theme.*

class WatchlistWidget : GlanceAppWidget() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WatchlistWidgetEntryPoint {
        fun moviesRepository(): MoviesRepository
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WatchlistWidgetEntryPoint::class.java
        )
        val repository = entryPoint.moviesRepository()

        provideContent {
            var watchlistWithBitmaps by remember { mutableStateOf<List<WatchlistItemData>>(emptyList()) }

            LaunchedEffect(Unit) {
                repository.getWatchlist().collect { list ->
                    val topItems = list.reversed().take(5)
                    val itemsWithBitmaps = topItems.map { item ->
                        val posterUrl = "https://image.tmdb.org/t/p/w185${item.posterPath}"
                        val bitmap = loadWidgetPosterBitmap(context, posterUrl)
                        WatchlistItemData(item, bitmap)
                    }
                    watchlistWithBitmaps = itemsWithBitmaps
                }
            }

            GlanceTheme(colors = FilmyWidgetColorScheme) {
                WatchlistWidgetContent(
                    watchlist = watchlistWithBitmaps,
                    onItemClick = { data ->
                        val item = data.movie
                        val uri = if (item.type == 1) {
                            "http://tech.salroid.com/shows/${item.id}"
                        } else {
                            "http://tech.salroid.com/movies/${item.id}"
                        }
                        val intent = Intent(Intent.ACTION_VIEW, uri.toUri()).apply {
                            `package` = context.packageName
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        actionStartActivity(intent)
                    }
                )
            }
        }
    }
}

data class WatchlistItemData(val movie: MovieDetails, val bitmap: Bitmap?)

@Composable
fun WatchlistWidgetContent(
    watchlist: List<WatchlistItemData>,
    onItemClick: (WatchlistItemData) -> Action
) {
    val context = LocalContext.current
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                provider = ImageProvider(R.mipmap.ic_launcher),
                contentDescription = null,
                modifier = GlanceModifier.size(24.dp)
            )
            Spacer(modifier = GlanceModifier.width(8.dp))
            Text(
                text = context.getString(R.string.widget_watchlist_title),
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = GlanceModifier.height(12.dp))

        if (watchlist.isEmpty()) {
            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = context.getString(R.string.widget_watchlist_empty),
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                )
            }
        } else {
            LazyColumn(
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                items(watchlist) { item ->
                    WatchlistItem(
                        data = item,
                        onClick = onItemClick(item)
                    )
                }
            }
        }
    }
}

@Composable
private fun WatchlistItem(
    data: WatchlistItemData,
    onClick: Action
) {
    val item = data.movie
    val bitmap = data.bitmap
    val context = LocalContext.current

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .padding(8.dp)
            .clickable(onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            provider = if (bitmap != null) ImageProvider(bitmap) else ImageProvider(R.drawable.movie_skeleton),
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = GlanceModifier
                .width(56.dp)
                .height(80.dp)
                .cornerRadius(8.dp)
        )

        Spacer(modifier = GlanceModifier.width(12.dp))

        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = item.title ?: context.getString(R.string.unknown_title),
                style = TextStyle(
                    color = GlanceTheme.colors.onSecondaryContainer,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1
            )
            Text(
                text = if (item.type == 1) context.getString(R.string.media_type_show) else context.getString(R.string.media_type_movie),
                style = TextStyle(
                    color = GlanceTheme.colors.onSecondaryContainer,
                    fontSize = 12.sp
                )
            )
        }

        Image(
            provider = ImageProvider(R.drawable.ic_check),
            contentDescription = context.getString(R.string.cd_mark_watched),
            modifier = GlanceModifier
                .size(28.dp)
                .padding(4.dp)
                .clickable(
                    actionRunCallback<MarkWatchedAction>(
                        actionParametersOf(
                            movieIdKey to item.id,
                            movieTypeKey to item.type
                        )
                    )
                )
        )
    }
}

private val FilmyWidgetColorScheme = ColorProviders(
    light = lightScheme,
    dark = darkScheme
)

class WatchlistWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WatchlistWidget()
}
