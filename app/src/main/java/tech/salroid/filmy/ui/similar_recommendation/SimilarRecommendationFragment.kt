package tech.salroid.filmy.ui.similar_recommendation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.details.MovieDetailsActivity
import tech.salroid.filmy.ui.adapters.SimilarMoviesAdapter
import tech.salroid.filmy.data.local.model.SimilarMovie
import tech.salroid.filmy.databinding.SimilarFragmentBinding
import tech.salroid.filmy.ui.details.TvDetailsActivity
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.FROM_ACTIVITY
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_ID
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_TITLE
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.NETWORK_APPLICABLE

@AndroidEntryPoint
class SimilarRecommendationFragment : Fragment() {

    private lateinit var viewModel: SimilarRecommendationViewModel
    private lateinit var suggestionType: SuggestionType
    private var movieId: String? = null
    private var movieTitle: String? = null

    private var _binding: SimilarFragmentBinding? = null
    private val binding get() = _binding!!
    private var isTv: Boolean? = false

    enum class SuggestionType {
        SIMILAR,
        RECOMMENDATION
    }

    companion object {
        const val TAG_SIMILAR = "SIMILAR"
        const val TAG_RECOMMENDATION = "RECOMMENDATION"
        const val IS_TV = "IS_TV"
        private const val SUGGESTION_TYPE = "SUGGESTION_TYPE"

        fun newInstance(
            id: String?,
            title: String?,
            suggestionType: SuggestionType,
            isTv: Boolean = false
        ): SimilarRecommendationFragment {
            val fragment = SimilarRecommendationFragment()
            val args = Bundle()
            args.putString(MOVIE_ID, id)
            args.putString(MOVIE_TITLE, title)
            args.putBoolean(IS_TV, isTv)
            args.putSerializable(SUGGESTION_TYPE, suggestionType)
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
        viewModel = ViewModelProvider(requireActivity())[SimilarRecommendationViewModel::class.java]
        binding.recyclerView.visibility = View.INVISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        suggestionType = (arguments?.getSerializable(SUGGESTION_TYPE) as? SuggestionType)
            ?: SuggestionType.SIMILAR
        movieId = arguments?.getString(MOVIE_ID)
        movieTitle = arguments?.getString(MOVIE_TITLE)
        isTv = arguments?.getBoolean(IS_TV, false)

        fixLabelText()
        collectUiState()

        movieId?.let {
            when (suggestionType) {
                SuggestionType.SIMILAR -> {
                    if (isTv == true) viewModel.getSimilarTv(it) else viewModel.getSimilar(it)
                }
                SuggestionType.RECOMMENDATION -> {
                    if (isTv == true) viewModel.getRecommendationTv(it) else viewModel.getRecommendation(
                        it
                    )
                }
            }
        }
    }

    private fun fixLabelText() {
        binding.suggestionLabel.text = when (suggestionType) {
            SuggestionType.SIMILAR -> getString(R.string.similar)
            SuggestionType.RECOMMENDATION -> getString(R.string.recommendation)
        }
    }

    private fun collectUiState() {
        when (suggestionType) {
            SuggestionType.SIMILAR -> {
                lifecycleScope.launch {
                    viewModel.uiStateSimilar.collect { similarResponse ->
                        similarResponse?.let {
                            showSimilarMovies(it.results)
                        }
                    }
                }
            }
            SuggestionType.RECOMMENDATION -> {
                lifecycleScope.launch {
                    viewModel.uiStateRecommendation.collect { similarResponse ->
                        similarResponse?.let {
                            showSimilarMovies(it.results)
                        }
                    }
                }
            }
        }
    }

    private fun showSimilarMovies(similarMoviesList: List<SimilarMovie>) {
        val similarAdapter = SimilarMoviesAdapter { similarMoviesData, _ ->
            itemClicked(similarMoviesData)
        }.apply {
            submitList(similarMoviesList)
        }

        binding.recyclerView.adapter = similarAdapter
        binding.suggestionLabel.isVisible = similarMoviesList.isNotEmpty()
        binding.recyclerView.isVisible = similarMoviesList.isNotEmpty()
        binding.similarMain.isVisible = similarMoviesList.isNotEmpty()
    }

    private fun itemClicked(movie: SimilarMovie) {
        val detailClass =
            if (isTv == true) TvDetailsActivity::class.java else MovieDetailsActivity::class.java
        Intent(activity, detailClass).run {
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