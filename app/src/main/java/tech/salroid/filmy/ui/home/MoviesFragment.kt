package tech.salroid.filmy.ui.home

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.facebook.shimmer.Shimmer
import com.google.android.material.elevation.SurfaceColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.salroid.filmy.R
import tech.salroid.filmy.custom.materialsearchview.MaterialSearchView
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.data.local.db.entity.toMovieType
import tech.salroid.filmy.data.local.model.MoviesUiState
import tech.salroid.filmy.databinding.FragmentMoviesBinding
import tech.salroid.filmy.ui.adapters.MoviesAdapter
import tech.salroid.filmy.ui.details.MovieDetailsActivity
import tech.salroid.filmy.ui.search.SearchViewModelLegacy
import tech.salroid.filmy.utility.FilmyUtility.getGridLayoutManager
import tech.salroid.filmy.utility.isDarkThemeActivated
import tech.salroid.filmy.utility.showSnackBar

@AndroidEntryPoint
class MoviesFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val viewModelSearch: SearchViewModelLegacy by activityViewModels()
    private var adapter: MoviesAdapter? = null
    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!
    private var currentMovieType = Movie.MovieType.TRENDING

    companion object {
        const val MOVIE_ID = "MOVIE_ID"
        const val MOVIE_TITLE = "MOVIE_TITLE"
        const val FROM_ACTIVITY = "FROM_ACTIVITY"
        const val MOVIE_TYPE = "MOVIE_TYPE"
        const val DATABASE_APPLICABLE = "DATABASE_APPLICABLE"
        const val NETWORK_APPLICABLE = "NETWORK_APPLICABLE"
        const val SAVED_DATABASE_APPLICABLE = "SAVED_DATABASE_APPLICABLE"

        fun newInstance(movieType: Movie.MovieType): MoviesFragment {
            val args = Bundle()
            args.putSerializable(MOVIE_TYPE, movieType)
            val fragment = MoviesFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoviesBinding.inflate(inflater, container, false)
        initNavigationBar()
        themeSearchBar()
        setOnClickListener()
        return binding.root
    }

    private fun initNavigationBar() {
       /* val activeItemTag = viewModel.getCurrentSelectedMovieItem().toMovieTypeString()
        binding.mainAppBarContent.topNavigation.submitItems(
            items = getMoviesNavigationList(requireContext()),
            activeItemTag = activeItemTag,
            onClick = ::onNavigationItemSelected
        )*/
    }

    private fun setOnClickListener() {
        binding.trendingErrorView.retryButton.setOnClickListener {
            // viewModel.getAllMovies()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       //  setupSearch()
        setupRecyclerView()
        observerUiStates()
    }

    private fun setupRecyclerView() {
        adapter = MoviesAdapter { itemClicked(it) }.apply {
            registerAdapterDataObserver(object : AdapterDataObserver() {
                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    binding.recyclerTrending.post {
                        binding.recyclerTrending.scrollToPosition(0)
                    }
                }

                override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                    super.onItemRangeMoved(fromPosition, toPosition, itemCount)

                    binding.recyclerTrending.post {
                        binding.recyclerTrending.scrollToPosition(0)
                    }
                }
            })
        }

        binding.recyclerTrending.apply {
            layoutManager = getGridLayoutManager(requireContext())
            adapter = this@MoviesFragment.adapter
        }
    }

    private fun observerUiStates() {
        lifecycleScope.launch {
            viewModelSearch.uiStateCloseSearch.collect {
                if (it) {
                    //binding.searchView.closeSearch()
                    viewModelSearch.closeSearchDone()
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movies.collectLatest { data ->
                    binding.trendingSkeleton.root.isVisible = false
                    binding.trendingErrorView.root.isVisible = false
                    binding.recyclerTrending.isVisible = true
                    adapter?.submitData(data)
                }
            }
        }
    }

    private fun onNavigationItemSelected(tag: String) {
        when (val movieType = tag.toMovieType()) {
            null -> throw IllegalArgumentException("Wrong show movie tag: $tag")
            else -> viewModel.onMovieItemSelected(movieType)
        }
    }

    private fun updateUi(movieUiState: MoviesUiState) {
        when (movieUiState) {
            MoviesUiState.Loading -> {
                /*  if (viewModel.uiStateMovies.value is MoviesUiState.Loading) {
                      binding.recyclerTrending.removeAllViews()
                      binding.recyclerTrending.isVisible = false
                      binding.trendingSkeleton.root.isVisible = true
                      binding.trendingErrorView.root.isVisible = false
                  }*/
            }

            is MoviesUiState.Error -> {
                // Already Showing Previous Data
                if (adapter?.itemCount != 0) {
                    binding.root.showSnackBar(getString(R.string.something_went_wrong))
                } else {
                    binding.recyclerTrending.isVisible = false
                    binding.trendingSkeleton.root.isVisible = false
                    binding.trendingErrorView.root.isVisible = true
                }
            }

            is MoviesUiState.Success -> {
                //showMovies(movieUiState.movieResponse)
                currentMovieType = movieUiState.movieType
            }
        }
    }

    private suspend fun showMovies(moviesResponse: PagingData<Movie>) {
        binding.trendingSkeleton.root.isVisible = false
        binding.trendingErrorView.root.isVisible = false
        binding.recyclerTrending.isVisible = true
        adapter?.submitData(moviesResponse)
    }

    private fun themeSearchBar() {
        val color = SurfaceColors.SURFACE_3.getColor(requireActivity())
        //binding.searchView.setBackgroundColor(color)
    }

    /*private fun setupSearch() {
        binding.mainAppBarContent.searchButton.setOnClickListener {
            binding.mainAppBarContent.searchButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            viewModelSearch.searchViewVisible()

            lifecycleScope.launch {
                delay(100)
                *//*childFragmentManager.beginTransaction()
                    .replace(
                        R.id.search_container,
                        SearchFragment.newInstance(),
                        SearchFragment.TAG
                    )
                    .commit()*//*
                binding.mainAppBarContent.searchView.showSearch()
            }
        }

        binding.mainAppBarContent.searchView.setVoiceSearch(false)
        binding.mainAppBarContent.searchView.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() {
                lifecycleScope.launch {
                    viewModelSearch.isSearchOpen.value = true
                }
            }

            override fun onSearchViewClosed() {
                val fragment = childFragmentManager.findFragmentByTag(SearchFragment.TAG)
                fragment?.let {
                    childFragmentManager
                        .beginTransaction()
                        .remove(it)
                        .commit()
                }
                viewModelSearch.searchViewHidden()
            }
        })

        // Emit Search Query
        lifecycleScope.launch {
            binding.mainAppBarContent.searchView.setOnQueryTextListener(object :
                MaterialSearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    val trimmedQuery = newText.trim { it <= ' ' }
                    val finalQuery = trimmedQuery.replace(" ", "-")
                    viewModelSearch.query.value = finalQuery
                    return true
                }
            })
        }
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == AppCompatActivity.RESULT_OK) {
            val matches = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (matches != null && matches.size > 0) {
                val searchWrd = matches[0]
                if (!TextUtils.isEmpty(searchWrd)) {
                   // binding.mainAppBarContent.searchView.setQuery(searchWrd, false)
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()

        // TODO Optimise here
        // Update Shimmer Alpha Based On Theme
        val shimmerBuilder = Shimmer.AlphaHighlightBuilder()
            .setBaseAlpha(1.0f)
            .setDropoff(0.3f)
            .setIntensity(0.3f)
            .setHighlightAlpha(0.3f)

        if (requireActivity().isDarkThemeActivated()) {
            shimmerBuilder.setBaseAlpha(0.2f)
        }
        shimmerBuilder.build().let {
            //binding.trendingSkeleton.root.setShimmer(shimmerBuilder.build())
        }
        // val shimmerVisible = binding.moviesSkeletonContainer.root.isShimmerVisible
        // binding.moviesSkeletonContainer.root.showShimmer(shimmerVisible)
    }

    private fun itemClicked(movie: Movie) {
        Intent(activity, MovieDetailsActivity::class.java).run {
            putExtra(MOVIE_TITLE, movie.title)
            putExtra(FROM_ACTIVITY, true)
            putExtra(MOVIE_TYPE, currentMovieType)
            putExtra(DATABASE_APPLICABLE, true)
            putExtra(NETWORK_APPLICABLE, true)
            putExtra(MOVIE_ID, movie.id.toString())
            startActivity(this)
        }
        activity?.overridePendingTransition(0, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}


/* val checkedIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_check)
     binding.trendingToggle.addOnButtonCheckedListener { group, _, _ ->
         val timeWindow = when (group.checkedButtonId) {
             R.id.day -> {
                 binding.day.icon = checkedIcon
                 binding.week.icon = null
                 "day"
             }
             R.id.week -> {
                 binding.day.icon = null
                 binding.week.icon = checkedIcon
                 "week"
             }
             else -> "day"
         }

         lifecycleScope.launch {
             delay(100)
             viewModel.getTrending(timeWindow)
         }
}*/