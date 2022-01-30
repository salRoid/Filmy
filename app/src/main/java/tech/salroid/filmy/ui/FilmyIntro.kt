package tech.salroid.filmy.ui

import com.github.paolorotolo.appintro.AppIntro2
import android.os.Bundle
import androidx.fragment.app.Fragment
import tech.salroid.filmy.ui.activities.fragment.intro_fragments.IntroFragmentA
import tech.salroid.filmy.ui.activities.fragment.intro_fragments.IntroFragmentB
import tech.salroid.filmy.ui.activities.fragment.intro_fragments.IntroFragmentC
import tech.salroid.filmy.ui.activities.fragment.intro_fragments.IntroFragmentD

class FilmyIntro : AppIntro2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(IntroFragmentA())
        addSlide(IntroFragmentB())
        addSlide(IntroFragmentC())
        addSlide(IntroFragmentD())
        showStatusBar(false)

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