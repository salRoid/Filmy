package tech.salroid.filmy.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import tech.salroid.filmy.R;

public class FullScreenImage extends AppCompatActivity {

    private String image_url;
    private ImageView centreimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        centreimg=(ImageView)findViewById(R.id.cen_img);

        Intent intent = getIntent();
        if (intent != null) {
            image_url = intent.getStringExtra("img_url");
        }

        Glide.with(this)
                .load(image_url)
                .fitCenter()
                .into(centreimg);

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
