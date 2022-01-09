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
import tech.salroid.filmy.databinding.FragmentUpComingBinding
import tech.salroid.filmy.views.CustomToast

class UpComing : Fragment(), LoaderManager.LoaderCallbacks<Cursor?> {

    private var mainActivityAdapter: MainActivityAdapter? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var isShowingFromDatabase = false
    private var isInMultiWindowMode = false

    private lateinit var binding: FragmentUpComingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpComingBinding.inflate(inflater, container, false)
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
                            3,
                            GridLayoutManager.HORIZONTAL,
                            false
                        )
                        binding.recycler.layoutManager = gridLayoutManager
                    }
                    else -> {
                        if (isInMultiWindowMode) {
                            gridLayoutManager = GridLayoutManager(
                                context,
                                1,
                                GridLayoutManager.HORIZONTAL,
                                false
                            )
                            binding.recycler.layoutManager = gridLayoutManager
                        } else {
                            gridLayoutManager = GridLayoutManager(
                                context,
                                3,
                                GridLayoutManager.HORIZONTAL,
                                false
                            )
                            binding.recycler.layoutManager = gridLayoutManager
                        }
                    }
                }
            }
            else -> {
                when (activity?.resources?.configuration?.orientation) {
                    Configuration.ORIENTATION_PORTRAIT -> {
                        gridLayoutManager =
                            GridLayoutManager(
                                context,
                                1, GridLayoutManager.HORIZONTAL,
                                false
                            )
                        binding.recycler.layoutManager = gridLayoutManager
                    }
                    else -> {
                        if (isInMultiWindowMode) {
                            gridLayoutManager =
                                GridLayoutManager(
                                    context,
                                    1,
                                    GridLayoutManager.HORIZONTAL,
                                    false
                                )
                            binding.recycler.layoutManager = gridLayoutManager
                        } else {
                            gridLayoutManager =
                                GridLayoutManager(
                                    context,
                                    1, GridLayoutManager.HORIZONTAL,
                                    false
                                )
                            binding.recycler.layoutManager = gridLayoutManager
                        }
                    }
                }
            }
        }

        mainActivityAdapter = MainActivityAdapter { itemClicked(it) }
        binding.recycler.adapter = mainActivityAdapter
        return view
    }

    override fun onResume() {
        super.onResume()
        requireActivity().supportLoaderManager.initLoader(
            MovieProjection.UPCOMING_MOVIE_LOADER,
            null,
            this
        )
    }

    private fun itemClicked(cursor: Cursor) {
        val idIndex = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_ID)
        val titleIndex = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TITLE)

        val intent = Intent(activity, MovieDetailsActivity::class.java)
        intent.putExtra("title", cursor.getString(titleIndex))
        intent.putExtra("activity", true)
        intent.putExtra("type", 2)
        intent.putExtra("database_applicable", true)
        intent.putExtra("network_applicable", true)
        intent.putExtra("id", cursor.getString(idIndex))
        startActivity(intent)

        activity?.overridePendingTransition(0, 0)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor?> {
        val moviesForTheUri = FilmContract.UpComingMoviesEntry.CONTENT_URI
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
            mainActivityAdapter?.swapCursor(cursor)
            binding.breathingProgress.visibility = View.GONE
        } else if (!(activity as MainActivity).fetchingFromNetwork) {
            CustomToast.show(activity, "Failed to get Upcoming movies.", true)
            (activity as MainActivity?)?.cantProceed(-1)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor?>) {
        mainActivityAdapter?.swapCursor(null)
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        super.onMultiWindowModeChanged(isInMultiWindowMode)

        if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = if (isInMultiWindowMode) GridLayoutManager(
                context,
                1,
                GridLayoutManager.HORIZONTAL,
                false
            ) else GridLayoutManager(
                context,
                2, GridLayoutManager.HORIZONTAL,
                false
            )

            binding.recycler.layoutManager = gridLayoutManager
            binding.recycler.adapter = mainActivityAdapter
        }
    }
}