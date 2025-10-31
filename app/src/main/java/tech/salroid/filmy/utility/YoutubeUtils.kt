package tech.salroid.filmy.utility

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Generates the HTML string required to embed a YouTube video using an iframe.
 *
 * @param youtubeVideoId The unique identifier of the YouTube video.
 * @return A string containing the complete HTML to embed the YouTube video.
 */
fun getYouTubeIframeHTML(youtubeVideoId: String): String {
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
                                'iv_load_policy': 3, 
                                'playsinline': 1,
                                'origin': 'https://app.filmy.tech',
                                'enablejsapi': 1
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

private val client = OkHttpClient()

suspend fun isYoutubeShortVideo(videoId: String): Boolean =
    withContext(Dispatchers.IO) {
        val url = "https://www.youtube.com/watch?v=$videoId"

        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@withContext false

            val body = response.body?.string() ?: return@withContext false

            val shortsPattern = Regex("""href=["'](https://www\.youtube\.com/shorts/[^"']+)["']""")
            val match = shortsPattern.find(body)

            return@withContext match != null
        }
    }
