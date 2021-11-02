package tech.salroid.filmy.animations

import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout

object RevealAnimation {

    fun performReveal(allDetails: FrameLayout?) {
        if (allDetails != null) {
            val viewTreeObserver = allDetails.viewTreeObserver
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        circularRevealActivity(allDetails)
                        allDetails.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        }
    }

    private fun circularRevealActivity(allDetails: FrameLayout) {
        val cx = allDetails.width / 2
        val cy = allDetails.height / 2
        val finalRadius = allDetails.width.coerceAtLeast(allDetails.height).toFloat()

        // create the animator for this view (the start radius is zero)
        val circularReveal =
            ViewAnimationUtils.createCircularReveal(allDetails, cx, cy, 0f, finalRadius)
        circularReveal.duration = 1000

        // make the view visible and start the animation
        allDetails.visibility = View.VISIBLE
        circularReveal.start()
    }
}