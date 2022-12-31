package tech.salroid.filmy.ui.custom

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.FrameLayout
import tech.salroid.filmy.R
import kotlin.math.ceil

class BreathingProgress(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    init {
        if(!isInEditMode) {
            createProgress(context)
            invalidate()
            requestLayout()
            breathingAnimation()
        }
    }

    private fun breathingAnimation() {
        val breather = findViewById<View>(R.id.breather) as FrameLayout
        animate(breather)
    }

    private fun createProgress(context: Context) {


        val widthPxForFixedRing = getPx(50)
        val heightPxForFixedRing = getPx(50)

        val fixedRingLayout = FrameLayout(context)
        val layoutParams = LayoutParams(
            widthPxForFixedRing,
            heightPxForFixedRing,
            Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
        )

        fixedRingLayout.layoutParams = layoutParams
        fixedRingLayout.setBackgroundResource(R.drawable.awesome_filmy_progress)

        val widthPxForBreathingCircle = getPx(20)
        val heightPxForBreathingCircle = getPx(20)
        val breathingCircleLayout = FrameLayout(context)

        val layoutParamsBreathing = LayoutParams(
            widthPxForBreathingCircle,
            heightPxForBreathingCircle,
            Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
        )

        breathingCircleLayout.layoutParams = layoutParamsBreathing
        breathingCircleLayout.setBackgroundResource(R.drawable.filmy_circle)
        breathingCircleLayout.id = R.id.breather
        fixedRingLayout.addView(breathingCircleLayout)
        this.addView(fixedRingLayout)
    }

    private fun getPx(dp: Int): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val logicalDensity = displayMetrics.density
        return ceil((dp * logicalDensity).toDouble()).toInt()
    }

    private fun animate(view: View) {
        val mAnimation = ScaleAnimation(
            0.5f,
            1f,
            0.5f,
            1f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        mAnimation.duration = 1000
        mAnimation.repeatCount = -1
        mAnimation.repeatMode = Animation.REVERSE
        mAnimation.interpolator = AccelerateInterpolator()
        mAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
        })

        view.animation = mAnimation
    }
}