package tech.salroid.filmy.ui.activities.fragment

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.salroid.filmy.ui.activities.MovieDetailsActivity
import tech.salroid.filmy.ui.adapters.MoviesAdapter
import tech.salroid.filmy.data.local.db.FilmyDbHelper
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.databinding.FragmentUpComingBinding
import tech.salroid.filmy.data.network.NetworkUtil

class UpComing : Fragment() {
    private var moviesAdapter: MoviesAdapter? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var isShowingFromDatabase = false

    private var showingFromDb = false
    private lateinit var binding: FragmentUpComingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpComingBinding.inflate(inflater, container, false)
        val view = binding.root

        when (activity?.resources?.configuration?.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                gridLayoutManager =
                    GridLayoutManager(
                        context,
                        2,
                        GridLayoutManager.HORIZONTAL,
                        false
                    )
                binding.recycler.layoutManager = gridLayoutManager
            }
            else -> {
                gridLayoutManager =
                    GridLayoutManager(
                        context,
                        2,
                        GridLayoutManager.HORIZONTAL,
                        false
                    )
                binding.recycler.layoutManager = gridLayoutManager
            }
        }

        moviesAdapter = MoviesAdapter { itemClicked(it) }
        binding.recycler.adapter = moviesAdapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get movies from DB
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val movies = FilmyDbHelper
                .getDb(requireContext().applicationContext)
                .movieDao()
                .getAllUpcoming()

            if (movies.isNotEmpty()) {
                showMovies(movies)
                showingFromDb = true
            }
        }

        // Get movies from Network
        NetworkUtil.getUpComing({ moviesResponse ->
            moviesResponse?.results?.let {
                if(!showingFromDb) {
                    showMovies(it)
                }
                saveMoviesInDb(it)
            }
        }, {

        })
    }

    private fun saveMoviesInDb(movies: List<Movie>) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            movies.forEach {
                it.type = 2
            }

            FilmyDbHelper
                .getDb(requireContext().applicationContext)
                .movieDao()
                .insertAll(movies)
        }
    }

    private fun showMovies(movies: List<Movie>) {
        isShowingFromDatabase = true
        moviesAdapter?.swapData(movies)
    }

    private fun itemClicked(movie: Movie) {
        Intent(activity, MovieDetailsActivity::class.java).run {
            putExtra("title", movie.title)
            putExtra("activity", true)
            putExtra("type", 2)
            putExtra("database_applicable", true)
            putExtra("network_applicable", true)
            putExtra("id", movie.id.toString())
            startActivity(this)
            activity?.overridePendingTransition(0, 0)
        }
    }
}