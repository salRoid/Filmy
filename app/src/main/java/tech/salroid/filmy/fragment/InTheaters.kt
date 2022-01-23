package tech.salroid.filmy.fragment

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import tech.salroid.filmy.R
import tech.salroid.filmy.activities.MovieDetailsActivity
import tech.salroid.filmy.adapters.MoviesAdapter
import tech.salroid.filmy.data.MovieResult
import tech.salroid.filmy.data.MoviesResponse
import tech.salroid.filmy.databinding.FragmentInTheatersBinding
import tech.salroid.filmy.network.NetworkUtil

class InTheaters : Fragment() {

    private var adapter: MoviesAdapter? = null
    private var isShowingFromDatabase = false

    private var gridLayoutManager: GridLayoutManager? = null
    private var isInMultiWindowMode = false

    private lateinit var binding: FragmentInTheatersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentInTheatersBinding.inflate(inflater, container, false)
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
                                2,
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
                if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    gridLayoutManager =
                        GridLayoutManager(
                            context, 2, GridLayoutManager.HORIZONTAL,
                            false
                        )
                    binding.recycler.layoutManager = gridLayoutManager
                } else {
                    if (isInMultiWindowMode) {
                        gridLayoutManager =
                            GridLayoutManager(
                                context, 2, GridLayoutManager.HORIZONTAL,
                                false
                            )
                        binding.recycler.layoutManager = gridLayoutManager
                    } else {
                        gridLayoutManager =
                            GridLayoutManager(
                                context, 2, GridLayoutManager.HORIZONTAL,
                                false
                            )
                        binding.recycler.layoutManager = gridLayoutManager
                    }
                }
            }
        }
        adapter = MoviesAdapter { itemClicked(it) }
        binding.recycler.adapter = adapter
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NetworkUtil.getInTheatersMovies({
            showMovies(it)
        }, {

        })
    }

    private fun showMovies(moviesResponse: MoviesResponse?) {
        moviesResponse?.results?.let {
            isShowingFromDatabase = true
            adapter?.swapData(it)
        }
    }

    private fun itemClicked(movie: MovieResult) {
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        intent.putExtra("title", movie.title)
        intent.putExtra("activity", true)
        intent.putExtra("type", 1)
        intent.putExtra("database_applicable", true)
        intent.putExtra("network_applicable", true)
        intent.putExtra("id", movie.id.toString())
        startActivity(intent)

        activity?.overridePendingTransition(0, 0)
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        super.onMultiWindowModeChanged(isInMultiWindowMode)

        if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = if (isInMultiWindowMode) GridLayoutManager(
                context,
                2, GridLayoutManager.HORIZONTAL,
                false
            ) else GridLayoutManager(
                context, 3, GridLayoutManager.HORIZONTAL,
                false
            )
            binding.recycler.layoutManager = gridLayoutManager
            binding.recycler.adapter = adapter
        }
    }
}