package tech.salroid.filmy.ui.full

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.animation.DecelerateInterpolator
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import tech.salroid.filmy.data.local.model.TrailerData
import tech.salroid.filmy.ui.adapters.MovieTrailersAdapter
import tech.salroid.filmy.databinding.AllTrailerLayoutBinding
import tech.salroid.filmy.ui.full.YoutubePlayerActivity.Companion.VIDEO_ID
import tech.salroid.filmy.ui.full.YoutubePlayerActivity.Companion.VIDEO_TITLE
import tech.salroid.filmy.utility.FilmyUtility
import tech.salroid.filmy.utility.themeSystemBars
import kotlin.math.hypot

class AllTrailersFragment : Fragment() {

    private var movieTitle: String? = null
    private var trailers: Array<TrailerData>? = null
    private var _binding: AllTrailerLayoutBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val MOVIE_TITLE = "MOVIE_TITLE"
        const val TRAILERS = "TRAILERS"

        fun newInstance(title: String?, trailers: Array<TrailerData>): AllTrailersFragment {
            val args = Bundle()
            args.putString(MOVIE_TITLE, title)
            args.putParcelableArray(TRAILERS, trailers)
            val fragment = AllTrailersFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AllTrailerLayoutBinding.inflate(inflater, container, false)

        binding.cross.setOnClickListener {
            binding.cross.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            activity?.supportFragmentManager?.popBackStack()
        }

        binding.root.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int,
                oldRight: Int, oldBottom: Int
            ) {
                v.removeOnLayoutChangeListener(this)
                val cx = arguments?.getInt("cx") ?: 0
                val cy = arguments?.getInt("cy") ?: 0
                val radius = hypot(right.toDouble(), bottom.toDouble()).toInt()
                ViewAnimationUtils.createCircularReveal(v, cx, cy, 0f, radius.toFloat()).run {
                    interpolator = DecelerateInterpolator(2f)
                    duration = 1000
                    start()
                }
            }
        })

        requireActivity().themeSystemBars(
            lightStatusBar = true,
            isFullScreen = true,
            transparentStatus = true
        )

        setupNavSpace()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        movieTitle = arguments?.getString(MOVIE_TITLE, " ")
        val parcelableArray = arguments?.getParcelableArray(TRAILERS)
        trailers = parcelableArray?.filterIsInstance<TrailerData>()?.toTypedArray()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textViewTitle.text = movieTitle

        binding.allTrailerRecyclerView.adapter = trailers?.let {
            MovieTrailersAdapter(it) { trailerData ->
                trailerData.url?.let { id ->
                    playTrailerOnYoutube(id, trailerData.title)
                }
            }
        }
    }

    private fun setupNavSpace() {
        binding.root.doOnLayout {
            (binding.navSpace.layoutParams as ViewGroup.MarginLayoutParams).height =
                FilmyUtility.getNavigationBarHeight(requireActivity())
        }
    }

    private fun playTrailerOnYoutube(trailerId: String, trailerTitle: String?) {
        Intent(activity, YoutubePlayerActivity::class.java).run {
            putExtra(VIDEO_ID, trailerId)
            putExtra(VIDEO_TITLE, trailerTitle)
            startActivity(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        requireActivity().themeSystemBars(
            lightStatusBar = false,
            isFullScreen = true,
            transparentStatus = true
        )
    }
}