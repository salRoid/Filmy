package tech.salroid.filmy.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.R
import tech.salroid.filmy.activities.CharacterDetailsActivity
import tech.salroid.filmy.activities.FullCastActivity
import tech.salroid.filmy.adapters.CastAdapter
import tech.salroid.filmy.databinding.CastFragmentBinding
import tech.salroid.filmy.networking.TmdbVolleySingleton
import tech.salroid.filmy.parser.MovieDetailsActivityParseWork

class CastFragment : Fragment() {

    private var jsonCast: String? = null
    private var movieId: String? = null
    private var movieTitle: String? = null
    private var gotCrewListener: GotCrewListener? = null

    private var _binding: CastFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = CastFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.castRecycler.layoutManager = LinearLayoutManager(activity)
        binding.castRecycler.isNestedScrollingEnabled = false
        binding.castRecycler.visibility = View.INVISIBLE

        binding.more.setOnClickListener {
            if (jsonCast != null && movieTitle != null) {
                val intent = Intent(activity, FullCastActivity::class.java)
                intent.putExtra("cast_json", jsonCast)
                intent.putExtra("toolbar_title", movieTitle)
                startActivity(intent)
            }
        }

        return view
    }

    fun setGotCrewListener(gotCrewListener: GotCrewListener?) {
        this.gotCrewListener = gotCrewListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val savedBundle = arguments

        if (savedBundle != null) {
            movieId = savedBundle.getString("movie_id")
            movieTitle = savedBundle.getString("movie_title")
        }
        movieId?.let {
            getCastFromNetwork(it)
        }
    }

    fun getCastFromNetwork(movieId: String) {
        val url =
            "http://api.themoviedb.org/3/movie/$movieId/casts?api_key=${BuildConfig.TMDB_API_KEY}"

        val jsonObjectRequestForMovieCastDetails = JsonObjectRequest(url, null,
            { response: JSONObject ->
                jsonCast = response.toString()
                parseCastOutput(response.toString())
                if (gotCrewListener != null) gotCrewListener!!.gotCrew(response.toString())
            }
        ) {
            binding.breathingProgressFragment.visibility = View.GONE
        }
        val requestQueue = TmdbVolleySingleton.requestQueue
        requestQueue.add(jsonObjectRequestForMovieCastDetails)
    }

    private fun parseCastOutput(castResult: String) {
        val par = MovieDetailsActivityParseWork(castResult)
        val castList = par.parseCastMembers()

        val castAdapter = CastAdapter(castList, true) { castMemberDetailsData, _, view ->
            val intent = Intent(activity, CharacterDetailsActivity::class.java)
            intent.putExtra("id", castMemberDetailsData.castId)
            val p1 = Pair.create(view.findViewById<View>(R.id.cast_poster), "profile")
            val p2 = Pair.create(view.findViewById<View>(R.id.cast_name), "name")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, p1, p2)
            startActivity(intent, options.toBundle())
        }

        binding.castRecycler.adapter = castAdapter

        when {
            castList.size > 4 -> {
                binding.more.visibility = View.VISIBLE
            }
            castList.isEmpty() -> {
                binding.more.visibility = View.INVISIBLE
                binding.cardHolder.visibility = View.INVISIBLE
            }
            else -> {
                binding.more.visibility = View.INVISIBLE
            }
        }

        binding.breathingProgressFragment.visibility = View.GONE
        binding.castRecycler.visibility = View.VISIBLE
        binding.detailFragmentViewsLayout.minimumHeight = 0
    }

    interface GotCrewListener {
        fun gotCrew(crewData: String)
    }

    companion object {
        fun newInstance(movieId: String?, movieTitle: String?): CastFragment {
            val fragment = CastFragment()
            val args = Bundle()
            args.putString("movie_id", movieId)
            args.putString("movie_title", movieTitle)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}