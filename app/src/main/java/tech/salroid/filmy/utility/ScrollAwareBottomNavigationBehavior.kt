package tech.salroid.filmy.utility

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class ScrollAwareBottomNavigationBehavior(
    context: Context?,
    attrs: AttributeSet?
) : CoordinatorLayout.Behavior<BottomNavigationView?>(context, attrs) {

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: BottomNavigationView,
        dependency: View
    ): Boolean {
        return dependency is RecyclerView
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: BottomNavigationView,
        dependency: View
    ): Boolean {
        val dy = getDy(parent, dependency)
        if (dy > 0) {
            // Scrolling up
            child.visibility = View.GONE
        } else if (dy < 0) {
            // Scrolling down
            child.visibility = View.VISIBLE
        }
        return true
    }

    private fun getDy(parent: CoordinatorLayout, dependency: View): Int {
        var dy = 0
        for (i in 0 until parent.childCount) {
            val child: View = parent.getChildAt(i)
            if (child == dependency) {
                continue
            }
            if (child is RecyclerView) {
                dy += child.getTranslationY().toInt()
            }
        }
        return dy
    }
}