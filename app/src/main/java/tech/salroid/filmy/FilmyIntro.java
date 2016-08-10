package tech.salroid.filmy;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;

import tech.salroid.filmy.fragments.intro_fragments.IntroFragmentA;
import tech.salroid.filmy.fragments.intro_fragments.IntroFragmentB;
import tech.salroid.filmy.fragments.intro_fragments.IntroFragmentC;
import tech.salroid.filmy.fragments.intro_fragments.IntroFragmentD;

/**
 * Created by R Ankit on 09-08-2016.
 */

public class FilmyIntro extends AppIntro2 {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add your slide's fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.


        addSlide(new IntroFragmentA());
        addSlide(new IntroFragmentB());
        addSlide(new IntroFragmentC());
        addSlide(new IntroFragmentD());

        showStatusBar(false);
        setProgressButtonEnabled(true);


        //setZoomAnimation(); // OR
        //setFlowAnimation(); // OR
        //setSlideOverAnimation(); // OR
        setDepthAnimation();

        setVibrate(true);
        setVibrateIntensity(30);

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

}
