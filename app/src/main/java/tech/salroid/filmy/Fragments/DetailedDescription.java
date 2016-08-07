/*
package tech.salroid.filmy.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import java.util.HashMap;
import tech.salroid.filmy.R;
import tech.salroid.filmy.Utils.NullChecker;

*/
/**
 * Created by R Ankit on 07-08-2016.
 *//*


public class DetailedDescription extends Fragment implements View.OnClickListener {


    private TextView det_rating;
    private TextView det_released;
    private TextView det_certification;
    private TextView det_language;
    private TextView det_runtime;

    private ImageView youtube_link;
    private ImageView youtube_play_button;

    private LinearLayout trailorBackground;
    private TextView tvRating;
    private static HashMap<String,String> movieMap;

    private FrameLayout trailorView;
    private boolean trailer_boolean;
    private String trailer;
    private String TAG = DetailedDescription.class.getSimpleName();


    public static DetailedDescription newInstance(HashMap<String,String> mMovieMap) {
        DetailedDescription fragment = new DetailedDescription();

        movieMap = mMovieMap;
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.detailed_description,container,false);

        det_rating = (TextView) view.findViewById(R.id.detail_rating);
        det_released = (TextView) view.findViewById(R.id.detail_released);
        det_certification = (TextView) view.findViewById(R.id.detail_certification);
        det_runtime = (TextView) view.findViewById(R.id.detail_runtime);
        det_language = (TextView) view.findViewById(R.id.detail_language);

        youtube_link = (ImageView) view.findViewById(R.id.detail_youtube);
        youtube_play_button = (ImageView) view.findViewById(R.id.play_button);
        trailorBackground = (LinearLayout) view.findViewById(R.id.trailorBackground);
        tvRating = (TextView) view.findViewById(R.id.tvRating);
        trailorView = (FrameLayout) view.findViewById(R.id.trailorView);
        trailorView.setOnClickListener(this);

        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setAllThings(movieMap);

    }



    public void setAllThings(HashMap<String, String> mMovieMap) {




        if(movieMap!=null){

            movieMap = mMovieMap;

            String rating = movieMap.get("rating");
            String runtime = movieMap.get("runtime");
            String released = movieMap.get("released");
            String certification = movieMap.get("certification");
            String language = movieMap.get("language");

            trailer = movieMap.get("trailer");
            if (NullChecker.isSettable(rating))
               det_rating.setText(rating);

            if(runtime!=null && !runtime.equals("null mins"))
                det_runtime.setText(runtime);

            if (NullChecker.isSettable(released))
             det_released.setText(released);

            if (NullChecker.isSettable(certification))
            det_certification.setText(certification);

            if (NullChecker.isSettable(language))
            det_language.setText(language);

        }
    }

    public void doSwatchWork(Palette.Swatch trailorSwatch, final boolean trailer_boolean) {

        this.trailer_boolean = trailer_boolean;
        trailorBackground.setBackgroundColor(trailorSwatch.getRgb());
        tvRating.setTextColor(trailorSwatch.getTitleTextColor());
        det_rating.setTextColor(trailorSwatch.getBodyTextColor());


        String img_url = movieMap.get("img_url");

        Glide.with(this)
                .load(img_url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                        youtube_link.setImageBitmap(resource);
                        if (trailer_boolean)
                            youtube_play_button.setVisibility(View.VISIBLE);
                    }

                });


    }

    @Override
    public void onClick(View view) {

        if ((trailer_boolean))
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer)));

    }
}
*/
