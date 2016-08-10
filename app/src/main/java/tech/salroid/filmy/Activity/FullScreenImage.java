package tech.salroid.filmy.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import tech.salroid.filmy.Custom.BreathingProgress;
import tech.salroid.filmy.R;

public class FullScreenImage extends AppCompatActivity {

    private String image_url;
    private ImageView centreimg;
    private BreathingProgress breathingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        breathingProgress = (BreathingProgress) findViewById(R.id.breathingProgress);
        breathingProgress.setVisibility(View.VISIBLE);


        centreimg = (ImageView) findViewById(R.id.cen_img);

        Intent intent = getIntent();
        if (intent != null) {
            image_url = intent.getStringExtra("img_url");
        }

        Glide.with(this)
                .load(image_url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        centreimg.setImageBitmap(resource);
                        breathingProgress.setVisibility(View.INVISIBLE);
                    }
                });

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}