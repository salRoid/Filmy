package tech.salroid.filmy.Fragments;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import tech.salroid.filmy.R;

/**
 * Created by R Ankit on 29-07-2016.
 */

public class FullReadFragment extends Fragment implements View.OnClickListener {

    TextView title, desc;
    String titleValue, descValue;
    ImageView crossButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.read_full_layout, container, false);

        title = (TextView) view.findViewById(R.id.textViewTitle);
        desc = (TextView) view.findViewById(R.id.textViewDesc);
        crossButton = (ImageView) view.findViewById(R.id.cross);
        crossButton.setOnClickListener(this);


        // To run the animation as soon as the view is layout in the view hierarchy we add this
        // listener and remove it
        // as soon as it runs to prevent multiple animations if the view changes bounds
        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                       int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);

                int cx = getArguments().getInt("cx");
                int cy = getArguments().getInt("cy");

                // get the hypothenuse so the radius is from one corner to the other
                int radius = (int) Math.hypot(right, bottom);

                Animator reveal = null;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    reveal = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, radius);
                }
                reveal.setInterpolator(new DecelerateInterpolator(2f));
                reveal.setDuration(1000);
                reveal.start();
            }
        });

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        titleValue = getArguments().getString("title", " ");
        descValue = getArguments().getString("desc", " ");

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title.setText(titleValue);
        desc.setText(descValue);
    }

    @Override
    public void onClick(View view) {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

}
