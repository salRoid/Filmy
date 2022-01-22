package tech.salroid.filmy.ui.activities.fragment

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
import tech.salroid.filmy.ui.activities.CharacterDetailsActivity
import tech.salroid.filmy.ui.activities.FullCastActivity
import tech.salroid.filmy.ui.adapters.CastAdapter
import tech.salroid.filmy.data.local.Cast
import tech.salroid.filmy.data.local.Crew
import tech.salroid.filmy.databinding.CastFragmentBinding
import tech.salroid.filmy.data.network.NetworkUtil

class CastFragment : Fragment() {

    private var castList: ArrayList<Cast>? = null
    private var movieId: String? = null
    private var movieTitle: String? = null
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
            if (castList != null && movieTitle != null) {
                val intent = Intent(activity, FullCastActivity::class.java)
                intent.putExtra("cast_list", castList)
                intent.putExtra("toolbar_title", movieTitle)
                startActivity(intent)
            }
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val savedBundle = arguments

        if (savedBundle != null) {
            movieId = savedBundle.getString("movie_id")
            movieTitle = savedBundle.getString("movie_title")
        }
        movieId?.let {
            getCastAndCrew(it) {
                // TODO Show Crew
            }
        }
    }

    fun getCastAndCrew(movieId: String, crewCallback: (List<Crew>) -> Unit) {
        NetworkUtil.getCastAndCrew(movieId, { castCrewResponse ->
            castCrewResponse?.let { it1 ->
                this.castList = it1.cast
                showCasts(it1.cast)
                crewCallback(it1.crew)
            }
        }, {

        })
    }

    private fun showCasts(castList: ArrayList<Cast>) {

        val castAdapter = CastAdapter(castList, true) { castMemberDetailsData, _, view ->
            val intent = Intent(activity, CharacterDetailsActivity::class.java)
            intent.putExtra("id", castMemberDetailsData.id.toString())
            val p1 = Pair.create(view.findViewById<View>(R.id.cast_poster), "profile")
            val p2 = Pair.create(view.findViewById<View>(R.id.cast_name), "name")
            val options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), p1, p2)
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