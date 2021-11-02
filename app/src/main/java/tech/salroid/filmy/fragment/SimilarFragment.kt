package tech.salroid.filmy.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.JsonObjectRequest
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.activities.MovieDetailsActivity
import tech.salroid.filmy.custom_adapter.SimilarMovieActivityAdapter
import tech.salroid.filmy.data.SimilarMoviesData
import tech.salroid.filmy.databinding.SimilarFragmentBinding
import tech.salroid.filmy.networking.TmdbVolleySingleton
import tech.salroid.filmy.parser.MovieDetailsActivityParseWork

class SimilarFragment : Fragment(), SimilarMovieActivityAdapter.ClickListener {

    private var jsonSimilar: String? = null
    private var movieId: String? = null
    private var movieTitle: String? = null

    private var _binding: SimilarFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = SimilarFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        val llm = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.similarRecycler.layoutManager = llm
        binding.similarRecycler.isNestedScrollingEnabled = false
        binding.similarRecycler.visibility = View.INVISIBLE

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieId = arguments?.getString("movie_id")
        movieTitle = arguments?.getString("movie_title")

        movieId?.let {
            getSimilarFromNetwork(it)
        }
    }

    fun getSimilarFromNetwork(movieId: String) {
        val baseMovieCastDetails =
            "https://api.themoviedb.org/3/movie/$movieId/recommendations?api_key=${BuildConfig.TMDB_API_KEY}"

        val jsonObjectRequestForMovieCastDetails =
            JsonObjectRequest(baseMovieCastDetails, null, { response ->
                jsonSimilar = response.toString()
                parseSimilarOutput(response.toString())
            }, {
                binding.breathingProgressFragment.visibility = View.GONE
            })

        val volleySingleton = TmdbVolleySingleton.getInstance()
        val requestQueue = volleySingleton.requestQueue
        requestQueue.add(jsonObjectRequestForMovieCastDetails)
    }

    private fun parseSimilarOutput(similarMoviesResult: String) {
        val par = MovieDetailsActivityParseWork(similarMoviesResult)
        val similarMoviesList = par.parse_similar_movies()
        val similarAdapter = SimilarMovieActivityAdapter(activity, similarMoviesList, true)

        similarAdapter.setClickListener(this)
        binding.similarRecycler.adapter = similarAdapter

        if (similarMoviesList.size == 0) {
            binding.cardHolder.visibility = View.INVISIBLE
        }

        binding.breathingProgressFragment.visibility = View.GONE
        binding.similarRecycler.visibility = View.VISIBLE
        binding.detailFragmentViewsLayout.minimumHeight = 0
    }

    override fun itemClicked(movie: SimilarMoviesData, position: Int, view: View) {
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        intent.putExtra("title", movie.title)
        intent.putExtra("id", movie.id)
        intent.putExtra("network_applicable", true)
        intent.putExtra("activity", false)
        startActivity(intent)
    }

    companion object {
        fun newInstance(movie_Id: String?, movie_Title: String?): SimilarFragment {
            val fragment = SimilarFragment()
            val args = Bundle()
            args.putString("movie_id", movie_Id)
            args.putString("movie_title", movie_Title)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}