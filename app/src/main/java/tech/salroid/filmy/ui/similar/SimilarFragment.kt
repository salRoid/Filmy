package tech.salroid.filmy.ui.similar

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tech.salroid.filmy.ui.details.MovieDetailsActivity
import tech.salroid.filmy.ui.adapters.SimilarMoviesAdapter
import tech.salroid.filmy.data.local.model.SimilarMovie
import tech.salroid.filmy.databinding.SimilarFragmentBinding
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.FROM_ACTIVITY
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_ID
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_TITLE
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.NETWORK_APPLICABLE

@AndroidEntryPoint
class SimilarFragment : Fragment() {

    private lateinit var viewModel: SimilarViewModel
    private var movieId: String? = null
    private var movieTitle: String? = null

    private var _binding: SimilarFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(id: String?, title: String?): SimilarFragment {
            val fragment = SimilarFragment()
            val args = Bundle()
            args.putString(MOVIE_ID, id)
            args.putString(MOVIE_TITLE, title)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SimilarFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(requireActivity())[SimilarViewModel::class.java]
        binding.similarRecycler.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.similarRecycler.isNestedScrollingEnabled = false
        binding.similarRecycler.visibility = View.INVISIBLE

        observeUiState()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieId = arguments?.getString(MOVIE_ID)
        movieTitle = arguments?.getString(MOVIE_TITLE)

        movieId?.let {
            viewModel.getSimilar(it)
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.uiStateSimilar.collect { similarResponse ->
                similarResponse?.let {
                    showSimilarMovies(it.similars)
                }
            }
        }
    }

    private fun showSimilarMovies(similarMoviesList: List<SimilarMovie>) {
        val similarAdapter =
            SimilarMoviesAdapter(similarMoviesList) { similarMoviesData, _ ->
                itemClicked(similarMoviesData)
            }
        binding.similarRecycler.adapter = similarAdapter

        if (similarMoviesList.isEmpty()) {
            binding.cardHolder.visibility = View.INVISIBLE
        }

        binding.breathingProgressFragment.visibility = View.GONE
        binding.similarRecycler.visibility = View.VISIBLE
        binding.detailFragmentViewsLayout.minimumHeight = 0
    }

    private fun itemClicked(movie: SimilarMovie) {
        Intent(activity, MovieDetailsActivity::class.java).run {
            putExtra(MOVIE_TITLE, movie.title)
            putExtra(MOVIE_ID, movie.id.toString())
            putExtra(NETWORK_APPLICABLE, true)
            putExtra(FROM_ACTIVITY, false)
            startActivity(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}