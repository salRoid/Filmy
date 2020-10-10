package tech.salroid.filmy.customs;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;

import tech.salroid.filmy.R;

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


public class BreathingProgress extends FrameLayout {


    public BreathingProgress(Context context) {
        super(context);
        init(context);
    }


    //XML Inflation
    public BreathingProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

        //createMainProgressBar
        createProgress(context);

        //invalidate and redraw the views.
        invalidate();
        requestLayout();

        //nowShowBreathingAnimation
        breathingAnimation();
    }

    private void breathingAnimation() {

        FrameLayout breather = (FrameLayout) this.findViewById(R.id.breather);
        animate(breather);
    }


    private void createProgress(Context context) {


        Resources r = getResources();
        float dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 50, r.getDisplayMetrics());

        int widthPxForFixedRing = getPx(50);
        int heightPxForFixedRing = getPx(50);

        FrameLayout fixedRingLayout = new FrameLayout(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(widthPxForFixedRing, heightPxForFixedRing, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        fixedRingLayout.setLayoutParams(layoutParams);
        fixedRingLayout.setBackgroundResource(R.drawable.awesome_filmy_progress);


        int widthPxForBreathingCircle = getPx(20);
        int heightPxForBreathingCircle = getPx(20);


        FrameLayout breathingCircleLayout = new FrameLayout(context);
        FrameLayout.LayoutParams layoutParamsBreathing = new FrameLayout.LayoutParams(widthPxForBreathingCircle, heightPxForBreathingCircle, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        breathingCircleLayout.setLayoutParams(layoutParamsBreathing);
        breathingCircleLayout.setBackgroundResource(R.drawable.filmy_circle);
        breathingCircleLayout.setId(R.id.breather);


        fixedRingLayout.addView(breathingCircleLayout);
        this.addView(fixedRingLayout);

    }

    private int getPx(int dp) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay().getMetrics(displayMetrics);

        float logicalDensity = displayMetrics.density;

        return (int) Math.ceil(dp * logicalDensity);
    }


    public void animate(View view) {

        ScaleAnimation mAnimation = new ScaleAnimation(0.5f, 1f, 0.5f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setDuration(1000);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(new AccelerateInterpolator());
        mAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.setAnimation(mAnimation);
    }

}
