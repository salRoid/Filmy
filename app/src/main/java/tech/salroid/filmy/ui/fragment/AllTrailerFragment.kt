package tech.salroid.filmy.ui.activities.fragment

import android.animation.Animator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.youtube.player.YouTubeStandalonePlayer
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.adapters.TrailerAdapter
import tech.salroid.filmy.databinding.AllTrailerLayoutBinding
import kotlin.math.hypot

class AllTrailerFragment : Fragment(), View.OnClickListener {

    private var titleValue: String? = null
    private var trailers: Array<String>? = null
    private var trailersName: Array<String>? = null
    private lateinit var recyclerView: RecyclerView

    private var nightMode = false
    private var _binding: AllTrailerLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
        nightMode = sp.getBoolean("dark", false)
        _binding = AllTrailerLayoutBinding.inflate(inflater, container, false)
        val view = binding.root
        if (!nightMode) allThemeLogic() else nightModeLogic()

        binding.cross.setOnClickListener(this)

        view.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int,
                oldRight: Int, oldBottom: Int
            ) {
                v.removeOnLayoutChangeListener(this)
                val cx = arguments!!.getInt("cx")
                val cy = arguments!!.getInt("cy")

                val radius = hypot(right.toDouble(), bottom.toDouble())
                    .toInt()
                val reveal: Animator =
                    ViewAnimationUtils.createCircularReveal(v, cx, cy, 0f, radius.toFloat())
                reveal.interpolator = DecelerateInterpolator(2f)
                reveal.duration = 1000
                reveal.start()
            }
        })
        init(view)
        return view
    }

    private fun nightModeLogic() {
        binding.cross.setImageDrawable(
            context?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_action_navigation_close_inverted
                )
            }
        )
        binding.mainContent.setBackgroundColor(Color.parseColor("#212121"))
        binding.textViewTitle.setTextColor(Color.parseColor("#ffffff"))
    }

    private fun allThemeLogic() {
        binding.cross.setImageDrawable(
            context?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_close_black_48dp
                )
            }
        )
        binding.mainContent.setBackgroundColor(Color.parseColor("#ffffff"))
        binding.textViewTitle.setTextColor(Color.parseColor("#000000"))
    }

    private fun init(view: View) {
        recyclerView = view.findViewById<View>(R.id.all_trailer_recycler_view) as RecyclerView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        titleValue = arguments?.getString("title", " ")
        trailers = arguments?.getStringArray("trailers")
        trailersName = arguments?.getStringArray("trailers_name")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        recyclerView.layoutManager = linearLayoutManager
        binding.textViewTitle.text = titleValue

        val trailerAdapter = trailers?.let {
            trailersName?.let { it1 ->
                TrailerAdapter(it, it1) { trailerId ->
                    trailerItemClicked(trailerId)
                }
            }
        }
        recyclerView.adapter = trailerAdapter
    }

    override fun onClick(view: View) {
        fragmentManager?.popBackStack()
    }

    private fun trailerItemClicked(trailerId: String) {
        val timeMilliSeconds = 0
        val autoPlay = true
        val lightBoxMode = false

        startActivity(
            YouTubeStandalonePlayer.createVideoIntent(
                activity,
                BuildConfig.YOUTUBE_API_KEY,
                trailerId,
                timeMilliSeconds,
                autoPlay,
                lightBoxMode
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}