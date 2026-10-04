package tech.salroid.filmy.ui.widget

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil3.asDrawable
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult

suspend fun loadWidgetPosterBitmap(context: Context, url: String): Bitmap? {
    return try {
        val loader = context.imageLoader
        val request = ImageRequest.Builder(context)
            .data(url)
            .size(200, 300) // Small size for widget to save memory
            .build()
        val result = loader.execute(request)
        if (result is SuccessResult) {
            result.image.asDrawable(context.resources).toBitmap()
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}
