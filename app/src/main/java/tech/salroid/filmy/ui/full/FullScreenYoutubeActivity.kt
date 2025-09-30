package tech.salroid.filmy.ui.full

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
// Removed unnecessary Build import if not used directly
import android.os.Bundle
import android.view.View
// Removed unnecessary WindowInsets and WindowInsetsController imports if System UI is handled by compat
import android.webkit.WebChromeClient // Keep this
import android.webkit.WebView
import android.webkit.WebViewClient
//import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
// Removed ViewCompat if not directly used after initial setup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
// Removed Glance visibility if not used
import tech.salroid.filmy.R
import tech.salroid.filmy.databinding.ActivityFullScreenYoutubeBinding // Ensure this is correct

class FullScreenYoutubeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullScreenYoutubeBinding
    private var youtubeVideoId: String? = null
    private var videoTitle: String? = null
    private var html5VideoView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null
    private lateinit var activityWebChromeClient: WebChromeClient // Declare as member variable

    companion object {
        const val VIDEO_ID = "extra_video_id"
        const val VIDEO_TITLE = "extra_video_title"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        //enableEdgeToEdge()
        binding = ActivityFullScreenYoutubeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUI()

        youtubeVideoId = intent.getStringExtra(VIDEO_ID)
        videoTitle = intent.getStringExtra(VIDEO_TITLE)

        binding.videoTitleTextView.text = videoTitle ?: getString(R.string.video_player)

        // Initialize the member WebChromeClient
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
                if (html5VideoView == null) {
                    return
                }
                binding.videoFullscreenContainer.removeView(html5VideoView)
                binding.videoFullscreenContainer.visibility = View.GONE
                html5VideoView = null
                customViewCallback?.onCustomViewHidden()
                customViewCallback = null
                binding.headerContainer.visibility = View.VISIBLE
                binding.webviewContainer.visibility = View.VISIBLE
                hideSystemUI()
            }
        }

        youtubeVideoId?.let { videoId ->
            binding.youtubeWebView.apply {
                settings.javaScriptEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.setSupportZoom(false)
                settings.builtInZoomControls = false
                settings.displayZoomControls = false
                webViewClient = WebViewClient()
                webChromeClient = activityWebChromeClient // Assign the member instance
                loadData(getYouTubeIframeHTML(videoId), "text/html", "utf-8")
            }
        }

        binding.closeButton.setOnClickListener {
            finish()
        }
    }

    private fun getYouTubeIframeHTML(videoId: String): String {
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
                            videoId: '$videoId',
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
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        hideSystemUI()
    }

    override fun onBackPressed() {
        if (html5VideoView != null) {
            activityWebChromeClient.onHideCustomView() // Now correctly calls the member instance
        } else if (binding.youtubeWebView.canGoBack()) {
            binding.youtubeWebView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
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
        binding.youtubeWebView.destroy()
        super.onDestroy()
    }
}

