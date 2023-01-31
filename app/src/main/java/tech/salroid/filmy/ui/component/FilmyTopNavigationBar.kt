package tech.salroid.filmy.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import tech.salroid.filmy.R

class FilmyTopNavigationBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    private val parent by lazy {
        LayoutInflater.from(context)
            .inflate(R.layout.top_navigation_layout, this, false) as LinearLayout
    }
    private val dimen6dp by lazy { context.resources.getDimensionPixelSize(R.dimen.filmy6dp) }
    private val spaceViewStart by lazy(LazyThreadSafetyMode.NONE) {
        View(context).apply {
            layoutParams = ViewGroup.LayoutParams(dimen6dp, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
    private val spaceViewEnd by lazy(LazyThreadSafetyMode.NONE) {
        View(context).apply {
            layoutParams = ViewGroup.LayoutParams(dimen6dp, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
    private var lastActiveItemTag: String? = null

    init {
        addView(parent)
    }

    fun submitItems(
        items: List<NavigationItemData>,
        activeItemTag: String? = null,
        onClick: ((String) -> Unit)? = null
    ) {
        with(parent) {
            removeAllViews()
            addView(spaceViewStart)
            items.forEachIndexed { i, itemData ->
                addView(
                    FilmyTopNavigationBarItem(context).apply {
                        setTag(itemData.tag)
                        setLabel(itemData.label)
                        setOnClickListener {
                            if (itemData.tag == lastActiveItemTag) return@setOnClickListener
                            setActiveItem(itemData.tag)
                            onClick?.invoke(itemData.tag)
                        }
                    }
                )
                if (i == 0 && activeItemTag == null) setActiveItem(itemData.tag)
            }
            addView(spaceViewEnd)
        }

        // Make this item as active
        activeItemTag?.let {
            setActiveItem(it)
        }
    }

    fun setActiveItem(tag: String) {
        performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        val activeItem = parent.findViewWithTag<FilmyTopNavigationBarItem>(tag)
        activeItem?.makeItemActive()
        lastActiveItemTag?.let {
            parent.findViewWithTag<FilmyTopNavigationBarItem>(it)?.makeItemInActive()
        }
        lastActiveItemTag = tag
        post { smoothScrollToItem(activeItem) }
    }

    private fun smoothScrollToItem(item: FilmyTopNavigationBarItem) {
        val newX = item.left + (item.width / 2) - width / 2
        smoothScrollTo(newX, item.y.toInt())
    }
}

data class NavigationItemData(val tag: String, val label: String)