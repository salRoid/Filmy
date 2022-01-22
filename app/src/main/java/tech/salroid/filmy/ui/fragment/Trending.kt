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
import tech.salroid.filmy.data.local.database.FilmyDbHelper
import tech.salroid.filmy.data.local.database.entity.Movie
import tech.salroid.filmy.databinding.FragmentTrendingBinding
import tech.salroid.filmy.data.network.NetworkUtil

class Trending : Fragment() {

    private var adapter: MoviesAdapter? = null
    private var gridLayoutManager: GridLayoutManager? = null

    //private var isInMultiWindowMode = false
    var isShowingFromDatabase = false
    private var showingFromDb = false

    private lateinit var binding: FragmentTrendingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrendingBinding.inflate(inflater, container, false)
        val view = binding.root

        //val tabletSize = resources.getBoolean(R.bool.isTablet)
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        // isInMultiWindowMode = activity?.isInMultiWindowMode == true
        //}

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
                gridLayoutManager = GridLayoutManager(
                    context,
                    1,
                    GridLayoutManager.HORIZONTAL,
                    false
                )
                binding.recycler.layoutManager = gridLayoutManager
            }
        }
        adapter = MoviesAdapter { itemClicked(it) }
        binding.recycler.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get movies from DB
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val movies = FilmyDbHelper
                .getDb(requireContext().applicationContext)
                .movieDao()
                .getAllTrending()
            if (movies.isNotEmpty()) {
                showMovies(movies)
                showingFromDb = true
            }
        }

        // Get movies from Network
        NetworkUtil.getTrendingMovies({ moviesResponse ->
            moviesResponse?.results?.let {
                if(!showingFromDb){
                   showMovies(it)
                }
                saveMoviesInDb(it)
            }
        }, {
        })
    }

    private fun saveMoviesInDb(movies: List<Movie>) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            FilmyDbHelper
                .getDb(requireContext().applicationContext)
                .movieDao()
                .insertAll(movies)
        }
    }

    private fun showMovies(movies: List<Movie>) {
        isShowingFromDatabase = true
        adapter?.swapData(movies)
    }

    private fun itemClicked(movie: Movie) {
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        intent.putExtra("title", movie.title)
        intent.putExtra("activity", true)
        intent.putExtra("type", 0)
        intent.putExtra("database_applicable", true)
        intent.putExtra("network_applicable", true)
        intent.putExtra("id", movie.id.toString())
        startActivity(intent)

        activity?.overridePendingTransition(0, 0)
    }
}