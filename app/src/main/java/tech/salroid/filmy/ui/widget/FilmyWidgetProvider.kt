package tech.salroid.filmy.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.AppWidgetTarget
import kotlinx.coroutines.*
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.details.MovieDetailsActivity
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.DATABASE_APPLICABLE
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.FROM_ACTIVITY
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_ID
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_TITLE
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_TYPE
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.NETWORK_APPLICABLE
import tech.salroid.filmy.ui.home.MoviesRepository

//@AndroidEntryPoint
class FilmyWidgetProvider : AppWidgetProvider() {

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    //@Inject
    lateinit var moviesRepository: MoviesRepository

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        coroutineScope.launch {
            //val movies = moviesRepository.getTrendingFromLocal()
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidget = AppWidgetManager.getInstance(context)
            val appWidgetIds =
                appWidget.getAppWidgetIds(ComponentName(context, FilmyWidgetProvider::class.java))

           /* if (movies.isNotEmpty() && appWidgetIds.isNotEmpty()) {
                val movie = movies.first()
                updateAppWidget(
                    context,
                    appWidgetManager,
                    appWidgetIds,
                    movie.id,
                    movie.title,
                    movie.backdropPath
                )
            }*/
        }
    }

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
        movieId: Int,
        movieTitle: String?,
        moviePoster: String?
    ) {

        //val radius = context.resources.getDimensionPixelSize(R.dimen.filmy8dp)
        val remoteViews = RemoteViews(context.packageName, R.layout.filmy_appwidget).apply {
            setTextViewText(R.id.widget_movie_name, movieTitle)
        }

        val appWidgetTarget = AppWidgetTarget(
            context.applicationContext,
            R.id.widget_movie_image,
            remoteViews,
            *appWidgetIds
        )

        Glide.with(context.applicationContext)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            //.transform(RoundedCorners(radius))
            .load("https://image.tmdb.org/t/p/w1280$moviePoster")
            .into(appWidgetTarget)

        remoteViews.setOnClickPendingIntent(
            android.R.id.background,
            getPendingIntentActivity(context, movieId, movieTitle)
        )
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews)
    }

    private fun getPendingIntentActivity(
        context: Context,
        movieId: Int,
        movieTitle: String?
    ): PendingIntent? {
        val intent = Intent(context, MovieDetailsActivity::class.java).apply {
            putExtra(MOVIE_ID, movieId.toString())
            putExtra(MOVIE_TITLE, movieTitle)
            putExtra(FROM_ACTIVITY, true)
            putExtra(MOVIE_TYPE, -1)
            putExtra(DATABASE_APPLICABLE, false)
            putExtra(NETWORK_APPLICABLE, true)
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        job.cancel()
    }
}
