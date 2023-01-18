package tech.salroid.filmy.ui.intro

import com.github.paolorotolo.appintro.AppIntro2
import android.os.Bundle
import androidx.fragment.app.Fragment

class FilmyIntroActivity : AppIntro2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(IntroFragmentA())
        addSlide(IntroFragmentB())
        addSlide(IntroFragmentC())
        addSlide(IntroFragmentD())

        showStatusBar(true)
        isProgressButtonEnabled = true
        setDepthAnimation()
    }

    override fun onSkipPressed(currentFragment: Fragment) {
        super.onSkipPressed(currentFragment)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment) {
        super.onDonePressed(currentFragment)
        finish()
    }
}