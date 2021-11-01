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
import tech.salroid.filmy.R
import tech.salroid.filmy.activities.CharacterDetailsActivity
import tech.salroid.filmy.activities.FullCrewActivity
import tech.salroid.filmy.custom_adapter.CrewAdapter
import tech.salroid.filmy.data_classes.CrewDetailsData
import tech.salroid.filmy.databinding.CrewFragmentBinding
import tech.salroid.filmy.parser.MovieDetailsActivityParseWork

class CrewFragment : Fragment(), CrewAdapter.ClickListener {

    private var jsonCrew: String? = null
    private var movieId: String? = null
    private var movieTitle: String? = null

    private var _binding: CrewFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CrewFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.crewRecycler.layoutManager = LinearLayoutManager(activity)
        binding.crewRecycler.isNestedScrollingEnabled = false
        binding.crewRecycler.visibility = View.INVISIBLE

        binding.crewMore.setOnClickListener {
            if (jsonCrew != null && movieTitle != null) {
                val intent = Intent(activity, FullCrewActivity::class.java)
                intent.putExtra("crew_json", jsonCrew)
                intent.putExtra("toolbar_title", movieTitle)
                startActivity(intent)
            }
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        movieId = arguments?.getString("movie_id")
        movieTitle = arguments?.getString("movie_title")
    }

    fun parseCrewOutput(crewResult: String?) {
        val par = MovieDetailsActivityParseWork(activity, crewResult)
        val crewList = par.parse_crew()
        jsonCrew = crewResult

        binding.crewRecycler.adapter = CrewAdapter(activity, crewList, true).apply {
            setClickListener(this@CrewFragment)
        }

        when {
            crewList.size > 4 -> {
                binding.crewMore.visibility = View.VISIBLE
            }
            crewList.size == 0 -> {
                binding.crewMore.visibility = View.INVISIBLE
                binding.cardHolder.visibility = View.INVISIBLE
            }
            else -> {
                binding.crewMore.visibility = View.INVISIBLE
            }
        }

        binding.breathingProgressFragment.visibility = View.GONE
        binding.crewRecycler.visibility = View.VISIBLE
        binding.detailFragmentViewsLayout.minimumHeight = 0
    }


    override fun itemClicked(setterGetter: CrewDetailsData, position: Int, view: View) {
        val intent = Intent(activity, CharacterDetailsActivity::class.java)
        intent.putExtra("id", setterGetter.crewId)

        val p1 = Pair.create(view.findViewById<View>(R.id.crew_poster), "profile")
        val p2 = Pair.create(view.findViewById<View>(R.id.crew_name), "name")
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, p1, p2)

        startActivity(intent, options.toBundle())
    }

    companion object {
        fun newInstance(movieId: String?, movieTitle: String?): CrewFragment {
            val fragment = CrewFragment()
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