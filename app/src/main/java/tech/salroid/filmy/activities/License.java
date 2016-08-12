package tech.salroid.filmy.activities;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;

public class License extends AppCompatActivity {

    @BindView(R.id.glide)
    TextView glide;
    @BindView(R.id.materialsearcview)
    TextView material;
    @BindView(R.id.circularimageview)
    TextView circularimageview;
    @BindView(R.id.tatarka)
    TextView tatarka;
    @BindView(R.id.error)
    TextView error;
    @BindView(R.id.appintro)
    TextView appintro;
    @BindView(R.id.butterknife)
    TextView butterknife;
    @BindView(R.id.crashlytics)
    TextView crashlytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= 24) {
            glide.setText(Html.fromHtml(getString(R.string.glide), Html.FROM_HTML_MODE_LEGACY));
            material.setText(Html.fromHtml(getString(R.string.materialsearch), Html.FROM_HTML_MODE_LEGACY));
            circularimageview.setText(Html.fromHtml(getString(R.string.circularimageview), Html.FROM_HTML_MODE_LEGACY));
            tatarka.setText(Html.fromHtml(getString(R.string.tatarka), Html.FROM_HTML_MODE_LEGACY));
            error.setText(Html.fromHtml(getString(R.string.errorview), Html.FROM_HTML_MODE_LEGACY));
            appintro.setText(Html.fromHtml(getString(R.string.appintro), Html.FROM_HTML_MODE_LEGACY));
            butterknife.setText(Html.fromHtml(getString(R.string.butterknife), Html.FROM_HTML_MODE_LEGACY));
            crashlytics.setText(Html.fromHtml(getString(R.string.crashlytics), Html.FROM_HTML_MODE_LEGACY));
        } else {
            glide.setText(Html.fromHtml(getString(R.string.glide)));
            material.setText(Html.fromHtml(getString(R.string.materialsearch)));
            circularimageview.setText(Html.fromHtml(getString(R.string.circularimageview)));
            tatarka.setText(Html.fromHtml(getString(R.string.tatarka)));
            error.setText(Html.fromHtml(getString(R.string.errorview)));
            appintro.setText(Html.fromHtml(getString(R.string.appintro)));
            butterknife.setText(Html.fromHtml(getString(R.string.butterknife)));
            crashlytics.setText(Html.fromHtml(getString(R.string.crashlytics)));
        }



    }
}
