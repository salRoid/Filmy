package tech.salroid.filmy.fragment

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.volley.toolbox.JsonObjectRequest
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.R
import tech.salroid.filmy.activities.CharacterDetailsActivity
import tech.salroid.filmy.activities.MovieDetailsActivity
import tech.salroid.filmy.custom_adapter.SearchResultAdapter
import tech.salroid.filmy.data.SearchData
import tech.salroid.filmy.databinding.FragmentSearchBinding
import tech.salroid.filmy.networking.VolleySingleton
import tech.salroid.filmy.parser.SearchResultParseWork

class SearchFragment : Fragment(), SearchResultAdapter.ClickListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root

        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
        val nightMode = sp.getBoolean("dark", false)
        if (nightMode) binding.fragmentRl.setBackgroundColor(activity!!.resources.getColor(R.color.black))
        else binding.fragmentRl.setBackgroundColor(
            activity!!.resources.getColor(R.color.grey)
        )

        val tabletSize = resources.getBoolean(R.bool.isTablet)
        when {
            tabletSize -> {
                when (activity!!.resources.configuration.orientation) {
                    Configuration.ORIENTATION_PORTRAIT -> {
                        val gridLayoutManager = StaggeredGridLayoutManager(
                            6,
                            StaggeredGridLayoutManager.VERTICAL
                        )
                        binding.searchResultsRecycler.layoutManager = gridLayoutManager
                    }
                    else -> {
                        val gridLayoutManager = StaggeredGridLayoutManager(
                            8,
                            StaggeredGridLayoutManager.VERTICAL
                        )
                        binding.searchResultsRecycler.layoutManager = gridLayoutManager
                    }
                }
            }
            else -> {
                when (activity!!.resources.configuration.orientation) {
                    Configuration.ORIENTATION_PORTRAIT -> {
                        val gridLayoutManager = StaggeredGridLayoutManager(
                            3,
                            StaggeredGridLayoutManager.VERTICAL
                        )
                        binding.searchResultsRecycler.layoutManager = gridLayoutManager
                    }
                    else -> {
                        val gridLayoutManager = StaggeredGridLayoutManager(
                            5,
                            StaggeredGridLayoutManager.VERTICAL
                        )
                        binding.searchResultsRecycler.layoutManager = gridLayoutManager
                    }
                }
            }
        }
        return view
    }

    override fun itemClicked(setterGetter: SearchData, position: Int) {
        val intent: Intent
        if (setterGetter.type == "person") intent =
            Intent(activity, CharacterDetailsActivity::class.java) else {
            intent = Intent(activity, MovieDetailsActivity::class.java)
            intent.putExtra("network_applicable", true)
        }
        intent.putExtra("title", setterGetter.movie)
        intent.putExtra("id", setterGetter.id)
        intent.putExtra("activity", false)
        startActivity(intent)
    }

    fun getSearchedResult(query: String) {
        val trimmedQuery = query.trim { it <= ' ' }
        val finalQuery = trimmedQuery.replace(" ", "-")
        val volleySingleton = VolleySingleton.getInstance()
        val requestQueue = volleySingleton.requestQueue

        val baseUrl =
            "https://api.themoviedb.org/3/search/movie?api_key=${BuildConfig.TMDB_API_KEY}&query=$finalQuery"
        val jsonObjectRequest = JsonObjectRequest(baseUrl, null, { response ->
            parseSearchedOutput(response.toString())
        }) {}
        requestQueue.add(jsonObjectRequest)
    }

    private fun parseSearchedOutput(results: String) {
        val park = SearchResultParseWork(results)
        val list = park.parseSearchData()

        val adapter = SearchResultAdapter(activity, list)
        binding.searchResultsRecycler.adapter = adapter
        adapter.setClickListener(this)

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
        if (activity != null && activity!!.currentFocus != null) {
            val inputMethodManager =
                activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}