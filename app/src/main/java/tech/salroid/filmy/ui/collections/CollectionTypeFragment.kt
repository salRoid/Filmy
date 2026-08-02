package tech.salroid.filmy.ui.collections

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.databinding.FragmentCollectionMoviesBinding
import tech.salroid.filmy.ui.details.MovieDetailsActivity
import tech.salroid.filmy.ui.adapters.CollectionsAdapter
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_ID
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_TITLE
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.NETWORK_APPLICABLE
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.SAVED_DATABASE_APPLICABLE

@AndroidEntryPoint
class CollectionTypeFragment : Fragment() {

    private val viewModel: CollectionsViewModel by viewModels()
    private var adapter: CollectionsAdapter? = null
    private var _binding: FragmentCollectionMoviesBinding? = null
    private val binding get() = _binding!!
    private var currentCollectionType = CollectionType.FAVORITE

    enum class CollectionType {
        FAVORITE,
        WATCHLIST
    }

    companion object {
        const val COLLECTION_TYPE = "COLLECTION_TYPE"

        fun newInstance(collectionType: CollectionType): CollectionTypeFragment {
            val args = Bundle()
            args.putSerializable(COLLECTION_TYPE, collectionType)
            val fragment = CollectionTypeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionMoviesBinding.inflate(inflater, container, false)
        val view = binding.root

        currentCollectionType = arguments?.getSerializable(COLLECTION_TYPE) as CollectionType
        adapter = CollectionsAdapter(clickListener = { movieId, title ->
            itemClicked(movieId, title)
        }) { dataCursor, position ->
            itemLongClicked(dataCursor, position)
        }
        binding.mySavedRecycler.adapter = adapter
        collectUiStates()
        return view
    }

    override fun onResume() {
        super.onResume()

        viewModel.getFavorites()
        viewModel.getWatchLists()
    }

    private fun collectUiStates() {
        lifecycleScope.launch {
            when (currentCollectionType) {
                CollectionType.FAVORITE -> {
                    viewModel.uiStateFavorites.collect {
                        it?.let {
                            showMovies(it)
                        } ?: run {
                            binding.emptyContainer.isVisible = true
                        }
                    }
                }
                CollectionType.WATCHLIST -> {
                    viewModel.uiStateWatchlist.collect {
                        it?.let {
                            showMovies(it)
                        } ?: run {
                            binding.emptyContainerWatch.isVisible = true
                        }
                    }
                }
            }
        }
    }

    private fun showMovies(movies: List<MovieDetails>) {
        adapter?.submitList(movies.reversed())

        when (currentCollectionType) {
            CollectionType.FAVORITE -> binding.emptyContainer.isVisible = movies.isEmpty()
            CollectionType.WATCHLIST -> binding.emptyContainerWatch.isVisible = movies.isEmpty()
        }
    }

    private fun itemClicked(movieId: String, title: String?) {
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        intent.putExtra(SAVED_DATABASE_APPLICABLE, true)
        intent.putExtra(NETWORK_APPLICABLE, true)
        intent.putExtra(MOVIE_TITLE, title)
        intent.putExtra(MOVIE_ID, movieId)
        startActivity(intent)
    }

    private fun itemLongClicked(movie: MovieDetails, position: Int) {
        val adapter =
            ArrayAdapter<String>(requireActivity(), android.R.layout.simple_list_item_1).apply {
                add(getString(R.string.remove))
            }
        MaterialAlertDialogBuilder(requireContext()).run {
            setAdapter(adapter) { _: DialogInterface?, _: Int ->
                when (currentCollectionType) {
                    CollectionType.FAVORITE -> movie.favorite = false
                    CollectionType.WATCHLIST -> movie.watchlist = false
                }
                viewModel.updateMovieDetailsInDb(movie, position, currentCollectionType)
            }
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
