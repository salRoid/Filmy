package tech.salroid.filmy.ui.component

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textview.MaterialTextView
import tech.salroid.filmy.R

class FilmyTopNavigationBarItem @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val navigationItemContent =
        LayoutInflater.from(context).inflate(R.layout.top_navigation_item_content, this, true)
    private val navigationItemTitle: MaterialTextView =
        navigationItemContent.findViewById(R.id.navigationItemPrimaryTitle)
    private val navigationItemToggle: MaterialButtonToggleGroup =
        navigationItemContent.findViewById(R.id.navigationItemToggle)

    private val activeBackground by lazy(LazyThreadSafetyMode.NONE) {
        ContextCompat.getDrawable(context, R.drawable.top_navigation_bg)
    }
    private val activeBackgroundTint by lazy(LazyThreadSafetyMode.NONE) {
        //context.getColorFromAttr(R.attr.colorPrimaryContainer)
    }
    private val activeTextColor by lazy(LazyThreadSafetyMode.NONE) {
        // context.getColorFromAttr(R.attr.colorPrimary)
    }
    private val inActiveTextColor by lazy(LazyThreadSafetyMode.NONE) {
        // context.getColorFromAttr(R.attr.colorOnSurface)
    }

    private var currentState: NavigationState = NavigationState.INACTIVE
    private var drawable: Drawable? = null
    private var showNavigationToggle: Boolean = false

    enum class NavigationState {
        ACTIVE,
        INACTIVE
    }

    init {
        isClickable = true
        isFocusable = true
        setDefaultStyle()
        getAttributes()
    }

    private fun setDefaultStyle() {
        navigationItemTitle.setTextAppearance(R.style.TextAppearance_Filmy_LabelMedium)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        drawable = navigationItemTitle.compoundDrawables.getOrNull(0)
        navigationItemTitle.setCompoundDrawables(null, null, null, null)
    }

    private fun getAttributes() {
        context.withStyledAttributes(attrs, R.styleable.FilmyTopNavigationBarItem) {
            navigationItemTitle.text =
                getString(R.styleable.FilmyTopNavigationBarItem_navigationItemText)
            showNavigationToggle =
                getBoolean(R.styleable.FilmyTopNavigationBarItem_showNavigationToggle, false)
        }
    }

    fun setTag(tag: String) {
        this.tag = tag
    }

    fun setLabel(label: String) {
        navigationItemTitle.text = label
    }

    fun makeItemInActive() {
        currentState = NavigationState.INACTIVE
        navigationItemTitle.setTextAppearance(R.style.TextAppearance_Filmy_LabelMedium)
        //navigationItemTitle.setTextColor(inActiveTextColor)
        alpha = 0.8f
        drawable = navigationItemTitle.compoundDrawables.getOrNull(0)
        navigationItemTitle.setCompoundDrawables(null, null, null, null)
        //background = null
        //backgroundTintList = null
        navigationItemToggle.isVisible = false
    }

    fun makeItemActive() {
        currentState = NavigationState.ACTIVE
        navigationItemTitle.setTextAppearance(R.style.TextAppearance_Filmy_LabelMedium_Bold)
        // navigationItemTitle.setTextColor(activeTextColor)
        alpha = 1.0f

        drawable?.let {
            navigationItemTitle.setCompoundDrawables(drawable, null, null, null)
            // navigationItemTitle.compoundDrawables.getOrNull(0)?.let {
            //DrawableCompat.setTint(it, activeTextColor)
            // }
        }

        //background = activeBackground
        //backgroundTintList = ColorStateList.valueOf(activeBackgroundTint)
        navigationItemToggle.isVisible = showNavigationToggle
    }
}