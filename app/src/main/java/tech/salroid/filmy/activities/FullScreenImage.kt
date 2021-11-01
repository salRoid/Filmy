package tech.salroid.filmy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import android.graphics.Bitmap
import android.view.View
import com.bumptech.glide.request.transition.Transition
import tech.salroid.filmy.databinding.ActivityFullScreenImageBinding
import java.lang.Exception

class FullScreenImage : AppCompatActivity() {

    private var bannerImageUrl: String? = null
    private lateinit var binding: ActivityFullScreenImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.breathingProgress.visibility = View.VISIBLE
        bannerImageUrl = intent?.getStringExtra("img_url")

        try {
            Glide.with(this)
                .asBitmap()
                .load(bannerImageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(object : SimpleTarget<Bitmap?>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {
                        binding.ivCentered.setImageBitmap(resource)
                        binding.breathingProgress.visibility = View.INVISIBLE
                    }
                })
        } catch (e: Exception) {
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}