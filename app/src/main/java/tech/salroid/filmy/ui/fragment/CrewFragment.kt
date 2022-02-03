package tech.salroid.filmy.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import tech.salroid.filmy.ui.activities.CharacterDetailsActivity
import tech.salroid.filmy.ui.activities.FullCrewActivity
import tech.salroid.filmy.ui.adapters.CrewAdapter
import tech.salroid.filmy.data.local.model.Crew
import tech.salroid.filmy.databinding.CrewFragmentBinding

class CrewFragment : Fragment() {

    private var crewList: ArrayList<Crew>? = null
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
            if (crewList != null && movieTitle != null) {
                val intent = Intent(activity, FullCrewActivity::class.java)
                intent.putExtra("crew_list", crewList)
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

    fun showCrews(crewList: ArrayList<Crew>) {
        this.crewList = crewList
        binding.crewRecycler.adapter = CrewAdapter(crewList, true) { member, _, _ ->
            val intent = Intent(activity, CharacterDetailsActivity::class.java)
            intent.putExtra("id", member.id.toString())
            startActivity(intent)
        }

        when {
            crewList.size > 4 -> {
                binding.crewMore.visibility = View.VISIBLE
            }
            crewList.isEmpty() -> {
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