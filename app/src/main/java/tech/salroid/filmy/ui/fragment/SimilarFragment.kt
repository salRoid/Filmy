package tech.salroid.filmy.ui.activities.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import tech.salroid.filmy.ui.activities.MovieDetailsActivity
import tech.salroid.filmy.ui.adapters.SimilarMovieActivityAdapter
import tech.salroid.filmy.data.local.SimilarMovie
import tech.salroid.filmy.databinding.SimilarFragmentBinding
import tech.salroid.filmy.data.network.NetworkUtil

class SimilarFragment : Fragment() {

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
            getSimilarMovies(it)
        }
    }

    fun getSimilarMovies(movieId: String) {
        NetworkUtil.getSimilarMovies(movieId, { similars ->
            similars?.let {
                showSimilarMovies(it.similars)
            }
        }, {

        })
    }

      private fun showSimilarMovies(similarMoviesList: List<SimilarMovie>) {
          val similarAdapter = SimilarMovieActivityAdapter(similarMoviesList) { similarMoviesData, _ ->
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
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        intent.putExtra("title", movie.title)
        intent.putExtra("id", movie.id.toString())
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