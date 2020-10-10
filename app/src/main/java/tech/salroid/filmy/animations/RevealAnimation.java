package tech.salroid.filmy.animations;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

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

public class RevealAnimation {

    public static void performReveal(final FrameLayout allDetails) {

        if (allDetails != null) {

            ViewTreeObserver viewTreeObserver = allDetails.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        circularRevealActivity(allDetails);
                        allDetails.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        }

    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void circularRevealActivity(FrameLayout allDetails) {

        int cx = allDetails.getWidth() / 2;
        int cy = allDetails.getHeight() / 2;

        float finalRadius = Math.max(allDetails.getWidth(), allDetails.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(allDetails, cx, cy, 0, finalRadius);
        circularReveal.setDuration(1000);

        // make the view visible and start the animation
        allDetails.setVisibility(View.VISIBLE);
        circularReveal.start();
    }


}
