package tech.salroid.filmy.fragment

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import tech.salroid.filmy.databinding.ReadFullLayoutBinding
import kotlin.math.hypot

class FullReadFragment : Fragment() {

    private var titleValue: String? = null
    private var descValue: String? = null

    private var _binding: ReadFullLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = ReadFullLayoutBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.cross.setOnClickListener {
            if (fragmentManager != null) {
                fragmentManager?.popBackStack()
            }
        }

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
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        titleValue = arguments?.getString("title", " ")
        descValue = arguments?.getString("desc", " ")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewTitle.text = titleValue
        binding.textViewDesc.text = descValue
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}