package tech.salroid.filmy.ui.full

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
import android.graphics.Color
import android.os.Bundle
import android.view.HapticFeedbackConstants.VIRTUAL_KEY
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import tech.salroid.filmy.R
import tech.salroid.filmy.databinding.ActivityFullScreenYoutubeBinding
import tech.salroid.filmy.utility.getYouTubeIframeHTML

class YoutubePlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullScreenYoutubeBinding

    private var videoId: String? = null
    private var videoTitle: String? = null
    private var html5VideoView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null

    companion object {
        const val VIDEO_ID = "video_id"
        const val VIDEO_TITLE = "video_title"
        private const val YT_BASE_URL = "https://www.youtube.com"
        private const val MIME_TYPE = "text/html"
        private const val ENCODING = "utf-8"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        videoId = intent.getStringExtra(VIDEO_ID)
        videoTitle = intent.getStringExtra(VIDEO_TITLE)
        binding = ActivityFullScreenYoutubeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
        binding.videoTitleTextView.text = videoTitle ?: getString(R.string.video_player)
        binding.closeButton.setOnClickListener {
            binding.closeButton.performHapticFeedback(VIRTUAL_KEY)
            onBackPressedDispatcher.onBackPressed()
        }
        hideSystemUI()
        setupWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        videoId?.let { id ->
            binding.youtubeWebView.apply {
                setBackgroundColor(Color.TRANSPARENT)
                settings.apply {
                    javaScriptEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                    setSupportZoom(false)
                    builtInZoomControls = false
                    displayZoomControls = false
                }

                webViewClient = YTWebViewClient()
                webChromeClient = YTWebChromeClient()

                // Load the YouTube video iframe HTML
                loadDataWithBaseURL(
                    YT_BASE_URL,
                    getYouTubeIframeHTML(id),
                    MIME_TYPE,
                    ENCODING,
                    null
                )
            }
        }
    }

    private inner class YTWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            try {
                view?.context?.startActivity(
                    android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        request?.url
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return true
        }
    }

    private inner class YTWebChromeClient : WebChromeClient() {
        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
            if (html5VideoView != null) {
                onHideCustomView()
                return
            }
            html5VideoView = view
            customViewCallback = callback

            requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE

            binding.headerContainer.visibility = View.GONE
            binding.webviewContainer.visibility = View.GONE
            binding.youtubeWebView.visibility = View.GONE

            binding.videoFullscreenContainer.addView(html5VideoView)
            binding.videoFullscreenContainer.visibility = View.VISIBLE
            binding.videoFullscreenContainer.bringToFront()
        }

        override fun onHideCustomView() {
            if (html5VideoView == null) return
            requestedOrientation = SCREEN_ORIENTATION_UNSPECIFIED
            binding.videoFullscreenContainer.removeAllViews()
            html5VideoView = null
            customViewCallback?.onCustomViewHidden()
            customViewCallback = null

            binding.videoFullscreenContainer.visibility = View.GONE
            binding.webviewContainer.visibility = View.VISIBLE
            binding.youtubeWebView.visibility = View.VISIBLE
            binding.headerContainer.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        binding.youtubeWebView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.youtubeWebView.onPause()
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.main).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onDestroy() {
        binding.youtubeWebView.destroy()
        super.onDestroy()
    }
}