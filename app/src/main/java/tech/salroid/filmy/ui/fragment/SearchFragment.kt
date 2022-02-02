package tech.salroid.filmy.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import tech.salroid.filmy.ui.activities.MovieDetailsActivity
import tech.salroid.filmy.ui.adapters.SearchResultAdapter
import tech.salroid.filmy.data.local.model.SearchResult
import tech.salroid.filmy.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root

        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
        //val nightMode = sp.getBoolean("dark", false)

        when (activity?.resources?.configuration?.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                val gridLayoutManager = GridLayoutManager(
                    context,
                    3,
                )
                binding.searchResultsRecycler.layoutManager = gridLayoutManager
            }
            else -> {
                val gridLayoutManager = GridLayoutManager(
                    context,
                    5,
                )
                binding.searchResultsRecycler.layoutManager = gridLayoutManager
            }
        }
        return view
    }

    private fun itemClicked(searchData: SearchResult, position: Int) {
        Intent(activity, MovieDetailsActivity::class.java).run {
            putExtra("network_applicable", true)
            putExtra("title", searchData.originalTitle)
            putExtra("id", searchData.id.toString())
            putExtra("activity", false)
            startActivity(this)
        }
    }

    fun getSearchedResult(query: String) {

     /*   NetworkUtil.searchMovies(finalQuery, { searchResultResponse ->
            searchResultResponse?.let {
                showSearchResults(it.results)
            }
        }, {

        })*/
    }

    fun showSearchResults(results: List<SearchResult>) {
        val adapter = SearchResultAdapter(results) { searchData, position ->
            itemClicked(searchData, position)
        }
        binding.searchResultsRecycler.adapter = adapter

        hideProgress()
        hideSoftKeyboard()
    }

    fun showProgress() {
        binding.breathingProgress.visibility = View.VISIBLE
        binding.searchResultsRecycler.visibility = View.INVISIBLE
    }

    private fun hideProgress() {
        binding.breathingProgress.visibility = View.INVISIBLE
        binding.searchResultsRecycler.visibility = View.VISIBLE
    }

    private fun hideSoftKeyboard() {
        if (activity != null && activity?.currentFocus != null) {
            val inputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}