package tech.salroid.filmy.ui.fragment

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
import tech.salroid.filmy.data.local.db.FilmyDbHelper
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.data.network.NetworkUtil
import tech.salroid.filmy.databinding.FragmentInTheatersBinding
import tech.salroid.filmy.ui.activities.MovieDetailsActivity
import tech.salroid.filmy.ui.adapters.MoviesAdapter

class InTheaters : Fragment() {

    private var adapter: MoviesAdapter? = null
    private var isShowingFromDatabase = false
    private var gridLayoutManager: GridLayoutManager? = null
    private var showingFromDb = false

    private lateinit var binding: FragmentInTheatersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentInTheatersBinding.inflate(inflater, container, false)
        val view = binding.root

        if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager =
                GridLayoutManager(
                    context, 2,
                    GridLayoutManager.HORIZONTAL,
                    false
                )
            binding.recycler.layoutManager = gridLayoutManager
        } else {

            gridLayoutManager =
                GridLayoutManager(
                    context, 1,
                    GridLayoutManager.HORIZONTAL,
                    false
                )
            binding.recycler.layoutManager = gridLayoutManager
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
                .getAllInTheaters()

            if (movies.isNotEmpty()) {
                showMovies(movies)
                showingFromDb = true
            }
        }

        // Get movies from Network
        NetworkUtil.getInTheatersMovies({ moviesResponse ->
            moviesResponse?.results?.let {
                if (!showingFromDb) {
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
                it.type = 1
            }
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
        intent.putExtra("type", 1)
        intent.putExtra("database_applicable", true)
        intent.putExtra("network_applicable", true)
        intent.putExtra("id", movie.id.toString())
        startActivity(intent)

        activity?.overridePendingTransition(0, 0)
    }
}