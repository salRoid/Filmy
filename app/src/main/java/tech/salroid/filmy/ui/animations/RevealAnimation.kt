package tech.salroid.filmy.ui.animations

import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver.OnGlobalLayoutListener

object RevealAnimation {

    fun performReveal(view: View?) {
        if (view != null) {
            val viewTreeObserver = view.viewTreeObserver
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        circularRevealActivity(view)
                        view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        }
    }

    private fun circularRevealActivity(view: View) {
        val cx = view.width / 2
        val cy = view.height / 2
        val finalRadius = view.width.coerceAtLeast(view.height).toFloat()

        // create the animator for this view (the start radius is zero)
        val circularReveal =
            ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius)
        circularReveal.duration = 1000

        // make the view visible and start the animation
        view.visibility = View.VISIBLE
        circularReveal.start()
    }
}