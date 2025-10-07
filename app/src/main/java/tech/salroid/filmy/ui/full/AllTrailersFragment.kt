package tech.salroid.filmy.ui.full

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.TrailerData
import tech.salroid.filmy.databinding.AllTrailerLayoutBinding
import tech.salroid.filmy.ui.adapters.MovieTrailersAdapter
import tech.salroid.filmy.utility.themeSystemBars
import kotlin.math.hypot
import androidx.core.graphics.toColorInt

class AllTrailersFragment : Fragment() {

    private var trailerTitle: String? = null
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
        darkMode = isDarkMode()
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
                if (v.isAttachedToWindow) {
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

    private fun isDarkMode(): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val themeValue = preferences.getString("theme", "system")

        return when (themeValue) {
            "light" -> false
            "dark" -> true
            else -> { // system
                (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            }
        }
    }

    private fun nightModeLogic() {
        binding.mainContent.setBackgroundColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.surfaceColorDark
            )
        )
        binding.textViewTitle.setTextColor("#ffffff".toColorInt())
    }

    private fun allThemeLogic() {
        binding.mainContent.setBackgroundColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.surfaceColorLight
            )
        )
        binding.textViewTitle.setTextColor("#000000".toColorInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        trailerTitle = arguments?.getString(MOVIE_TITLE, " ")
        val parcelableArray = arguments?.getParcelableArray(TRAILERS)
        trailers = parcelableArray?.mapNotNull { it as? TrailerData }?.toTypedArray()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textViewTitle.text = trailerTitle

        binding.allTrailerRecyclerView.adapter = trailers?.let {
            MovieTrailersAdapter(it) { trailerData ->
                trailerData.url?.let { id ->
                    playTrailerOnYoutube(id, trailerTitle)
                }
            }
        }
    }

    private fun playTrailerOnYoutube(trailerId: String, trailerTitle: String?) {
        Intent(activity, YoutubePlayerActivity::class.java).run {
            putExtra(YoutubePlayerActivity.VIDEO_ID, trailerId)
            putExtra(YoutubePlayerActivity.VIDEO_TITLE, trailerTitle)
            startActivity(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.allTrailerRecyclerView.adapter = null
        requireActivity().themeSystemBars(!darkMode, lightStatusBar = false)
        _binding = null
    }
}