package tech.salroid.filmy.animations;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * Created by R Ankit on 06-08-2016.
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
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            allDetails.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            allDetails.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
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
