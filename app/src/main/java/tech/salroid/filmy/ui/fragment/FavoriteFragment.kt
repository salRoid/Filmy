package tech.salroid.filmy.ui.activities.fragment

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.salroid.filmy.ui.activities.MovieDetailsActivity
import tech.salroid.filmy.ui.adapters.SavedMoviesAdapter
import tech.salroid.filmy.data.local.db.FilmyDbHelper
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.databinding.FragmentFavMoviesBinding
import tech.salroid.filmy.utility.visible

class FavoriteFragment : Fragment() {

    private var adapter: SavedMoviesAdapter? = null
    private var _binding: FragmentFavMoviesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFavMoviesBinding.inflate(inflater, container, false)
        val view = binding.root

        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                val gridLayoutManager = StaggeredGridLayoutManager(
                    3,
                    StaggeredGridLayoutManager.VERTICAL
                )
                binding.mySavedRecycler.layoutManager = gridLayoutManager
            }
            else -> {
                val gridLayoutManager = StaggeredGridLayoutManager(
                    5,
                    StaggeredGridLayoutManager.VERTICAL
                )
                binding.mySavedRecycler.layoutManager = gridLayoutManager
            }
        }

        adapter = SavedMoviesAdapter(clickListener =
        { movieId, title ->
            itemClicked(movieId, title)
        })
        { dataCursor, position ->
            itemLongClicked(dataCursor, position)
        }
        binding.mySavedRecycler.adapter = adapter

        return view
    }


    override fun onResume() {
        super.onResume()
        // Get movies from DB
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val movies = FilmyDbHelper
                .getDb(requireContext().applicationContext)
                .movieDetailsDao()
                .getAllFavorites()

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                showMovies(movies)
                if (movies.isEmpty()) {
                    binding.emptyContainer.visible()
                }
            }
        }
    }

    private fun showMovies(movies: List<MovieDetails>) {
        adapter?.swapData(ArrayList(movies))
    }

    private fun itemClicked(movieId: String, title: String?) {
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        intent.putExtra("saved_database_applicable", true)
        intent.putExtra("network_applicable", true)
        intent.putExtra("title", title)
        intent.putExtra("id", movieId)
        startActivity(intent)
    }

    private fun itemLongClicked(movie: MovieDetails, position: Int) {
        val mad = MaterialAlertDialogBuilder(requireContext())
        val arrayAdapter =
            context?.let { ArrayAdapter<String>(it, android.R.layout.simple_list_item_1) }
        arrayAdapter?.add("Remove")
        mad.setAdapter(arrayAdapter) { _: DialogInterface?, _: Int ->
            movie.favorite = false
            updateMovieDetailsInDb(movie, position)
        }
        mad.show()
    }

    private fun updateMovieDetailsInDb(movie: MovieDetails, position: Int) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            FilmyDbHelper
                .getDb(requireContext().applicationContext)
                .movieDetailsDao()
                .updateDetails(movie)
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                adapter?.removeItemAt(position)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
