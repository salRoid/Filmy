package tech.salroid.filmy.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.Shimmer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.*
import tech.salroid.filmy.databinding.FragmentTvBinding
import tech.salroid.filmy.ui.adapters.TvAdapter
import tech.salroid.filmy.ui.component.NavigationItemData
import tech.salroid.filmy.ui.details.TvDetailsActivity
import tech.salroid.filmy.ui.search.SearchViewModel
import tech.salroid.filmy.utility.FilmyUtility.getGridLayoutManager
import tech.salroid.filmy.utility.isDarkThemeActivated
import tech.salroid.filmy.utility.showSnackBar

@AndroidEntryPoint
class TvFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val viewModelSearch: SearchViewModel by activityViewModels()
    private var adapter: TvAdapter? = null
    private var _binding: FragmentTvBinding? = null
    private val binding get() = _binding!!
    private var currentShowType = TvShow.ShowType.TRENDING

    companion object {
        const val MOVIE_ID = "MOVIE_ID"
        const val MOVIE_TITLE = "MOVIE_TITLE"
        const val FROM_ACTIVITY = "FROM_ACTIVITY"
        const val MOVIE_TYPE = "MOVIE_TYPE"
        const val DATABASE_APPLICABLE = "DATABASE_APPLICABLE"
        const val NETWORK_APPLICABLE = "NETWORK_APPLICABLE"

        fun newInstance(movieType: TvShow.ShowType): TvFragment {
            val args = Bundle()
            args.putSerializable(MOVIE_TYPE, movieType)
            val fragment = TvFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTvBinding.inflate(inflater, container, false)
        initNavigationBar()
        setupRecyclerViews()
        return binding.root
    }

    private fun initNavigationBar() {
        listOf(
            NavigationItemData(
                tag = TvShow.ShowType.TRENDING.toShowTypeString(),
                label = getString(R.string.tv_label_trending)
            ),
            /* NavigationItemData(
                   tag = TvShow.ShowType.POPULAR.toShowTypeString(),
                   label = getString(R.string.tv_label_popular)
               ),*/
            /* NavigationItemData(
                 tag = TvShow.ShowType.AIRING_TODAY.toShowTypeString(),
                 label = getString(R.string.tv_label_arriving_today)
             ),*/
            NavigationItemData(
                tag = TvShow.ShowType.ON_TV.toShowTypeString(),
                label = getString(R.string.tv_label_on_tv)
            ),
            NavigationItemData(
                tag = TvShow.ShowType.TOP_RATED.toShowTypeString(),
                label = getString(R.string.tv_label_top_rated)
            )
        ).let {
            val activeItemTag = viewModel.getCurrentSelectedTvItem().toShowTypeString()
            binding.mainAppBarContent.topNavigation.submitItems(
                items = it,
                activeItemTag = activeItemTag,
                onClick = ::onNavigationItemSelected
            )
        }
    }

    private fun setupRecyclerViews() {
        adapter = TvAdapter { itemClicked(it) }
        adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.recyclerTrending.post {
                    binding.recyclerTrending.smoothScrollToPosition(0)
                }
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                binding.recyclerTrending.post {
                    binding.recyclerTrending.scrollToPosition(0)
                }
            }
        })

        binding.recyclerTrending.apply {
            layoutManager = getGridLayoutManager(requireContext())
            adapter = this@TvFragment.adapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observerUiStates()
    }

    private fun observerUiStates() {
        /*lifecycleScope.launch {
            viewModel.uiStateTvShows.collect {
                updateUi(it)
            }
        }*/

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tvShows.collectLatest { data ->
                    binding.trendingSkeleton.root.isVisible = false
                    binding.trendingErrorView.root.isVisible = false
                    binding.recyclerTrending.isVisible = true
                    adapter?.submitData(data)
                }
            }
        }
    }

    private fun onNavigationItemSelected(tag: String) {
        when (val showType = tag.toShowType()) {
            null -> throw IllegalArgumentException("Wrong show type tag: $tag")
            else -> viewModel.onTvItemSelected(showType)
        }
    }

    private fun updateUi(tvUiState: TvShowUiState) {
        when (tvUiState) {
            TvShowUiState.Loading -> {
              /*  if (viewModel.uiStateMovies.value is MoviesUiState.Loading) {
                    binding.recyclerTrending.removeAllViews()
                    binding.recyclerTrending.isVisible = false
                    binding.trendingSkeleton.root.isVisible = true
                    binding.trendingErrorView.root.isVisible = false
                }*/
            }
            is TvShowUiState.Error -> {
                // Already Showing Previous Data
                if (adapter?.itemCount != 0) {
                    binding.root.showSnackBar(getString(R.string.something_went_wrong))
                } else {
                    binding.recyclerTrending.isVisible = false
                    binding.trendingSkeleton.root.isVisible = false
                    binding.trendingErrorView.root.isVisible = true
                }
            }
            is TvShowUiState.Success -> {
                // showTvShows(tvUiState.showResponse)
                currentShowType = tvUiState.showType
            }
        }
    }

    private fun showTvShows(tvShowResponse: TvShowResponse) {
        /* if (tvShowResponse.results.isNotEmpty()) {
             val currentList = adapter?.currentList?.toMutableList()
             // Reset All Previous Showing Tv Shows As Page Has Been Reset
             if (tvShowResponse.resetLocal && currentList?.isNotEmpty() == true) {
                 // binding.recyclerTrending.smoothScrollToPosition(0)
                 currentList.clear()
             }
             currentList?.addAll(tvShowResponse.results)
             val newList = currentList?.toList()
             adapter?.submitList(newList) {
                 binding.trendingSkeleton.root.isVisible = false
                 binding.trendingErrorView.root.isVisible = false
                 binding.recyclerTrending.isVisible = true
             }
         }*/
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
            // binding.trendingSkeleton.root.setShimmer(shimmerBuilder.build())
        }
    }

    private fun itemClicked(show: TvShow) {
        Intent(activity, TvDetailsActivity::class.java).run {
            putExtra(MOVIE_TITLE, show.name)
            putExtra(FROM_ACTIVITY, true)
            putExtra(MOVIE_TYPE, currentShowType)
            putExtra(DATABASE_APPLICABLE, true)
            putExtra(NETWORK_APPLICABLE, true)
            putExtra(MOVIE_ID, show.id.toString())
            startActivity(this)
        }
        activity?.overridePendingTransition(0, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}