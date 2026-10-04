package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImagesResponse(
    @SerialName("backdrops")
    var backdrops: List<ImageItem> = emptyList(),

    @SerialName("posters")
    var posters: List<ImageItem> = emptyList()
)

@Serializable
data class ImageItem(
    @SerialName("file_path")
    var filePath: String? = null,

    @SerialName("vote_average")
    var voteAverage: Double? = null
)
