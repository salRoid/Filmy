package tech.salroid.filmy;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;

import tech.salroid.filmy.fragment.intro_fragments.IntroFragmentA;
import tech.salroid.filmy.fragment.intro_fragments.IntroFragmentB;
import tech.salroid.filmy.fragment.intro_fragments.IntroFragmentC;
import tech.salroid.filmy.fragment.intro_fragments.IntroFragmentD;
/*
 * Filmy Application for Android
 * Copyright (c) 2016 Ramankit Singh (http://github.com/webianks).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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


        //setZoomAnimation(); //OR
        //setFlowAnimation(); //OR
        //setSlideOverAnimation(); //OR
        setDepthAnimation();

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
