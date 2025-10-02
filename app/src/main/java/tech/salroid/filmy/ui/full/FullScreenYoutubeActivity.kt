package tech.salroid.filmy.ui.full

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import tech.salroid.filmy.R
import tech.salroid.filmy.databinding.ActivityFullScreenYoutubeBinding
import tech.salroid.filmy.utility.themeSystemBars

class FullScreenYoutubeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullScreenYoutubeBinding

    private var darkMode = false
    private var videoId: String? = null
    private var videoTitle: String? = null
    private var videoType: String? = null
    private var html5VideoView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null
    private lateinit var activityWebChromeClient: WebChromeClient
    private val handler = Handler(Looper.getMainLooper())
    private val hideHeaderRunnable = Runnable { binding.headerContainer.visibility = View.GONE }

    companion object {
        const val VIDEO_ID = "video_id"
        const val VIDEO_TITLE = "video_title"
        const val VIDEO_TYPE = "video_type"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        videoId = intent.getStringExtra(VIDEO_ID)
        videoTitle = intent.getStringExtra(VIDEO_TITLE)
        videoType = intent.getStringExtra(VIDEO_TYPE)

        requestedOrientation = if (videoType == "true") {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        binding = ActivityFullScreenYoutubeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUI()

        binding.videoTitleTextView.text = videoTitle ?: getString(R.string.video_player)

        setupHeaderAutoHide()

        activityWebChromeClient = object : WebChromeClient() {
            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                if (html5VideoView != null) {
                    onHideCustomView()
                    return
                }
                html5VideoView = view
                customViewCallback = callback

                binding.headerContainer.visibility = View.GONE
                binding.webviewContainer.visibility = View.GONE
                binding.youtubeWebView.visibility = View.GONE

                binding.videoFullscreenContainer.addView(
                    html5VideoView,
                    android.widget.FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT
                    )
                )
                binding.videoFullscreenContainer.visibility = View.VISIBLE
                binding.videoFullscreenContainer.bringToFront()
                hideSystemUI()
            }

            override fun onHideCustomView() {
                if (html5VideoView == null) return

                binding.videoFullscreenContainer.removeAllViews()
                html5VideoView = null
                customViewCallback?.onCustomViewHidden()
                customViewCallback = null

                binding.videoFullscreenContainer.visibility = View.GONE
                binding.webviewContainer.visibility = View.VISIBLE
                binding.youtubeWebView.visibility = View.VISIBLE
                binding.headerContainer.visibility = View.VISIBLE

                hideSystemUI()
                setupHeaderAutoHide()
            }
        }

        videoId?.let { id ->
            binding.youtubeWebView.apply {
                settings.javaScriptEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.setSupportZoom(false)
                settings.builtInZoomControls = false
                settings.displayZoomControls = false
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                webViewClient = WebViewClient()
                webChromeClient = activityWebChromeClient
                loadDataWithBaseURL(
                    "https://www.youtube.com",
                    getYouTubeIframeHTML(id),
                    "text/html",
                    "utf-8",
                    null
                )
            }
        }

        binding.closeButton.setOnClickListener { finish() }
    }

    private fun setupHeaderAutoHide() {
        binding.headerContainer.visibility = View.VISIBLE
        binding.main.setOnClickListener {
            if (binding.headerContainer.visibility == View.VISIBLE) {
                binding.headerContainer.visibility = View.GONE
            } else {
                binding.headerContainer.visibility = View.VISIBLE
                handler.removeCallbacks(hideHeaderRunnable)
                handler.postDelayed(hideHeaderRunnable, 4000)
            }
        }

        handler.removeCallbacks(hideHeaderRunnable)
        handler.postDelayed(hideHeaderRunnable, 4000)
    }

    private fun getYouTubeIframeHTML(youtubeVideoId: String): String {
        return """
            <!DOCTYPE html>
            <html>
            <body style="margin:0px;padding:0px; background-color:black; overflow:hidden;">
                <div id="player" style="position:absolute; top:0; left:0; width:100%; height:100%;"></div>
                <script>
                    var player;
                    function onYouTubeIframeAPIReady() {
                        player = new YT.Player('player', {
                            height: '100%',
                            width: '100%',
                            videoId: '$youtubeVideoId',
                            playerVars: {
                                'autoplay': 1,
                                'controls': 1, 
                                'fs': 1,
                                'rel': 0,
                                'modestbranding': 1,
                                'iv_load_policy': 3, 
                                'showinfo': 0,      
                                'playsinline': 1     
                            },
                            events: {
                                'onReady': function(event) { event.target.playVideo(); }
                            }
                        });
                    }
                    var tag = document.createElement('script');
                    tag.src = "https://www.youtube.com/iframe_api";
                    var firstScriptTag = document.getElementsByTagName('script')[0];
                    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
                </script>
            </body>
            </html>
        """.trimIndent()
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.main).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        hideSystemUI()
    }

    override fun onBackPressed() {
        when {
            html5VideoView != null -> activityWebChromeClient.onHideCustomView()
            binding.youtubeWebView.canGoBack() -> binding.youtubeWebView.goBack()
            else -> super.onBackPressed()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    override fun onPause() {
        super.onPause()
        binding.youtubeWebView.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.youtubeWebView.onResume()
        hideSystemUI()
    }

    override fun onDestroy() {
        handler.removeCallbacks(hideHeaderRunnable)
        binding.youtubeWebView.destroy()
        super.onDestroy()
    }

}
