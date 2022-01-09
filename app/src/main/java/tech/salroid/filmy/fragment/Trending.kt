package tech.salroid.filmy.fragment

import android.content.Intent
import android.content.res.Configuration
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import tech.salroid.filmy.R
import tech.salroid.filmy.activities.MainActivity
import tech.salroid.filmy.activities.MovieDetailsActivity
import tech.salroid.filmy.adapters.MainActivityAdapter
import tech.salroid.filmy.database.FilmContract
import tech.salroid.filmy.database.MovieProjection
import tech.salroid.filmy.databinding.FragmentTrendingBinding
import tech.salroid.filmy.views.CustomToast

class Trending : Fragment(), LoaderManager.LoaderCallbacks<Cursor?> {

    private var adapter: MainActivityAdapter? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var isInMultiWindowMode = false
    var isShowingFromDatabase = false

    private lateinit var binding: FragmentTrendingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrendingBinding.inflate(inflater, container, false)
        val view = binding.root

        val tabletSize = resources.getBoolean(R.bool.isTablet)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            isInMultiWindowMode = activity?.isInMultiWindowMode == true
        }

        when {
            tabletSize -> {
                when (activity?.resources?.configuration?.orientation) {
                    Configuration.ORIENTATION_PORTRAIT -> {
                        gridLayoutManager = GridLayoutManager(
                            context,
                            2,
                            GridLayoutManager.HORIZONTAL,
                            false
                        )
                        binding.recycler.layoutManager = gridLayoutManager
                    }
                    else -> {
                        when {
                            isInMultiWindowMode -> {
                                gridLayoutManager = GridLayoutManager(
                                    context,
                                    6,
                                    GridLayoutManager.HORIZONTAL,
                                    false
                                )
                                binding.recycler.layoutManager = gridLayoutManager
                            }
                            else -> {
                                gridLayoutManager = GridLayoutManager(
                                    context,
                                    8,
                                    GridLayoutManager.HORIZONTAL,
                                    false
                                )
                                binding.recycler.layoutManager = gridLayoutManager
                            }
                        }
                    }
                }
            }
            else -> {
                when (activity?.resources?.configuration?.orientation) {
                    Configuration.ORIENTATION_PORTRAIT -> {
                        gridLayoutManager = GridLayoutManager(
                            context,
                            1,
                            GridLayoutManager.HORIZONTAL,
                            false
                        )
                        binding.recycler.layoutManager = gridLayoutManager
                    }
                    else -> {
                        when {
                            isInMultiWindowMode -> {
                                gridLayoutManager = GridLayoutManager(
                                    context,
                                    1,
                                    GridLayoutManager.HORIZONTAL,
                                    false
                                )
                                binding.recycler.layoutManager = gridLayoutManager
                            }
                            else -> {
                                gridLayoutManager = GridLayoutManager(
                                    context,
                                    1,
                                    GridLayoutManager.HORIZONTAL,
                                    false
                                )
                                binding.recycler.layoutManager = gridLayoutManager
                            }
                        }
                    }
                }
            }
        }
        adapter = MainActivityAdapter { itemClicked(it) }
        binding.recycler.adapter = adapter

        return view
    }

    override fun onResume() {
        super.onResume()
        activity?.supportLoaderManager?.initLoader(
            MovieProjection.TRENDING_MOVIE_LOADER,
            null,
            this
        )
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor?> {
        val moviesForTheUri = FilmContract.MoviesEntry.CONTENT_URI

        return CursorLoader(
            requireContext(),
            moviesForTheUri,
            MovieProjection.MOVIE_COLUMNS,
            null,
            null,
            null
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor?>, cursor: Cursor?) {
        if (cursor != null && cursor.count > 0) {
            isShowingFromDatabase = true
            adapter?.swapCursor(cursor)
            binding.breathingProgress.visibility = View.GONE
        } else if (!(activity as MainActivity).fetchingFromNetwork) {
            CustomToast.show(activity, "Failed to get latest movies.", true)
            (activity as MainActivity?)?.cantProceed(-1)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor?>) {
        adapter?.swapCursor(null)
    }

    private fun itemClicked(cursor: Cursor) {
        val idIndex = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_ID)
        val titleIndex = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TITLE)

        val intent = Intent(activity, MovieDetailsActivity::class.java)
        intent.putExtra("title", cursor.getString(titleIndex))
        intent.putExtra("activity", true)
        intent.putExtra("type", 0)
        intent.putExtra("database_applicable", true)
        intent.putExtra("network_applicable", true)
        intent.putExtra("id", cursor.getString(idIndex))
        startActivity(intent)

        activity?.overridePendingTransition(0, 0)
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        super.onMultiWindowModeChanged(isInMultiWindowMode)

        if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = if (isInMultiWindowMode) GridLayoutManager(
                context,
                2,
                GridLayoutManager.HORIZONTAL,
                false
            ) else GridLayoutManager(context, 3, GridLayoutManager.HORIZONTAL, false)
            binding.recycler.layoutManager = gridLayoutManager
            binding.recycler.adapter = adapter
        }
    }

    fun retryLoading() {
        activity?.supportLoaderManager?.restartLoader(
            MovieProjection.TRENDING_MOVIE_LOADER,
            null,
            this
        )
    }
}