package tech.salroid.filmy.utility

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class YoutubeUtils {

    private val client = OkHttpClient()

    suspend fun isYoutubeShortVideo(videoId: String): Boolean =
        withContext(Dispatchers.IO) {
            val url = "https://www.youtube.com/watch?v=$videoId"

            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext false

                val body = response.body()?.string() ?: return@withContext false

                val shortsPattern = Regex("""href=["'](https://www\.youtube\.com/shorts/[^"']+)["']""")
                val match = shortsPattern.find(body)

                return@withContext match != null
            }
        }


}
