package tech.salroid.filmy.ui.cast_crew

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.Cast
import tech.salroid.filmy.data.local.model.CastCrew
import tech.salroid.filmy.data.local.model.Crew
import tech.salroid.filmy.databinding.CastCrewFragmentBinding
import tech.salroid.filmy.ui.adapters.CastCrewAdapter
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_ID
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_TITLE

@AndroidEntryPoint
class CastCrewFragment : Fragment() {

    private lateinit var viewModel: CastCrewViewModel
    private var castCrewType: CastCrewType = CastCrewType.CAST
    private var castCrewList: ArrayList<CastCrew> = arrayListOf()
    private var castList: ArrayList<Cast>? = null
    private var crewList: ArrayList<Crew>? = null
    private var movieId: String? = null
    private var movieTitle: String? = null
    private var _binding: CastCrewFragmentBinding? = null
    private val binding get() = _binding!!

    enum class CastCrewType {
        CAST,
        CREW
    }

    companion object {
        const val CAST_CREW_TYPE = "CAST_OR_CREW"
        const val MEMBER_ID = "MEMBER_ID"
        const val CAST_CREW_LIST = "CAST_CREW_LIST"
        const val MOVIES = "MOVIES"
        const val TOOLBAR_TITLE = "TOOLBAR_TITLE"

        fun newInstance(
            movieId: String?,
            movieTitle: String?,
            castCrewType: CastCrewType
        ): CastCrewFragment {
            val fragment = CastCrewFragment()
            val args = Bundle()
            args.putSerializable(CAST_CREW_TYPE, castCrewType)
            args.putString(MOVIE_ID, movieId)
            args.putString(MOVIE_TITLE, movieTitle)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CastCrewFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(requireActivity())[CastCrewViewModel::class.java]
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.isNestedScrollingEnabled = false
        binding.recyclerView.isVisible = true

        binding.more.setOnClickListener {
            if (castCrewList.isNotEmpty() && movieTitle != null) {
                Intent(activity, AllCastCrewActivity::class.java).run {
                    putExtra(CAST_CREW_LIST, castCrewList)
                    putExtra(TOOLBAR_TITLE, movieTitle)
                    startActivity(this)
                }
            }
        }

        observeUiState()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieId = arguments?.getString(MOVIE_ID)
        movieTitle = arguments?.getString(MOVIE_TITLE)
        castCrewType = arguments?.getSerializable(CAST_CREW_TYPE) as CastCrewType

        val labelString = when (castCrewType) {
            CastCrewType.CAST -> getString(R.string.cast)
            CastCrewType.CREW -> getString(R.string.crew)
        }
        binding.memberTypeLabel.text = labelString

        movieId?.let {
            viewModel.getCastAndCrew(it)
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.uiStateCastAndCrew.collect { castAndCrewResponse ->
                castAndCrewResponse?.let {
                    when (castCrewType) {
                        CastCrewType.CAST -> {
                            castList = it.cast
                            showCastCrew(it.cast, emptyList())
                        }
                        CastCrewType.CREW -> {
                            crewList = it.crew
                            showCastCrew(emptyList(), it.crew)
                        }
                    }
                }
            }
        }
    }

    private fun showCastCrew(castList: List<Cast>, crewList: List<Crew>) {
        castCrewList.clear()

        castList.map {
            castCrewList.add(CastCrew.CastData(cast = it))
        }
        crewList.map {
            castCrewList.add(CastCrew.CrewData(crew = it))
        }

        binding.recyclerView.adapter =
            CastCrewAdapter(castCrewList, true) { castCrew, _, _ ->
                val id = when (castCrew) {
                    is CastCrew.CastData -> castCrew.cast.id
                    is CastCrew.CrewData -> castCrew.crew.id
                }
                val intent = Intent(activity, CastCrewDetailsActivity::class.java)
                intent.putExtra(MEMBER_ID, id.toString())
                startActivity(intent)
            }

        when {
            castCrewList.size > 4 -> {
                binding.more.visibility = View.VISIBLE
            }
            castCrewList.isEmpty() -> {
                binding.more.visibility = View.INVISIBLE
                binding.memberTypeLabel.visibility = View.INVISIBLE
            }
            else -> {
                binding.more.visibility = View.INVISIBLE
            }
        }

        binding.breathingProgressFragment.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        binding.detailFragmentViewsLayout.minimumHeight = 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}