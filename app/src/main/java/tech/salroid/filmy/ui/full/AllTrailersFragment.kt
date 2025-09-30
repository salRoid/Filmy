package tech.salroid.filmy.ui.full

import android.content.Intent // <-- Add this import
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.TrailerData
import tech.salroid.filmy.ui.adapters.MovieTrailersAdapter
import tech.salroid.filmy.databinding.AllTrailerLayoutBinding
import tech.salroid.filmy.ui.details.MovieDetailsViewModel
import tech.salroid.filmy.utility.PreferenceHelper.isDarkModeEnabled
import tech.salroid.filmy.utility.themeSystemBars
import kotlin.math.hypot

class AllTrailersFragment : Fragment() {

    private var movieTitle: String? = null
    private var trailers: Array<TrailerData>? = null
    private var darkMode = false
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
        darkMode = isDarkModeEnabled(requireContext())
        _binding = AllTrailerLayoutBinding.inflate(inflater, container, false)
        if (!darkMode) allThemeLogic() else nightModeLogic()

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
                if (v.isAttachedToWindow) { // Check if view is attached
                    ViewAnimationUtils.createCircularReveal(v, cx, cy, 0f, radius.toFloat()).run {
                        interpolator = DecelerateInterpolator(2f)
                        duration = 1000
                        start()
                    }
                }
            }
        })

        requireActivity().themeSystemBars(!darkMode, lightStatusBar = true)
        return binding.root
    }

    private fun nightModeLogic() {
        binding.mainContent.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.surfaceColorDark))
        binding.textViewTitle.setTextColor(Color.parseColor("#ffffff"))
    }

    private fun allThemeLogic() {
        binding.mainContent.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.surfaceColorLight))
        binding.textViewTitle.setTextColor(Color.parseColor("#000000"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        movieTitle = arguments?.getString(MOVIE_TITLE, " ")
        // Make sure the cast is safe
        val parcelableArray = arguments?.getParcelableArray(TRAILERS)
        trailers = parcelableArray?.mapNotNull { it as? TrailerData }?.toTypedArray()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textViewTitle.text = movieTitle

        binding.allTrailerRecyclerView.adapter = trailers?.let {
            MovieTrailersAdapter(it) { trailerData ->
                trailerData.url?.let { id ->
                    playTrailerOnYoutube(id, movieTitle)
                }
            }
        }
    }

    private fun playTrailerOnYoutube(trailerId: String, movieTitle: String?) {
        // Updated to launch FullScreenYoutubeActivity
        val isShortVideo = MovieDetailsViewModel.isYoutubeShortByUrlCheck(trailerId)

        val intent = Intent(activity, FullScreenYoutubeActivity::class.java).apply {
            putExtra(FullScreenYoutubeActivity.VIDEO_ID, trailerId)
            putExtra(FullScreenYoutubeActivity.VIDEO_TITLE, movieTitle)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.allTrailerRecyclerView.adapter = null
        requireActivity().themeSystemBars(!darkMode, lightStatusBar = false)
        _binding = null
    }
}
