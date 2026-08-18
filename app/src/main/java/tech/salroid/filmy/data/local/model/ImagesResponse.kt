package tech.salroid.filmy.data.local.model

import com.google.gson.annotations.SerializedName

data class ImagesResponse(
    @SerializedName("backdrops")
    var backdrops: List<ImageItem> = emptyList(),

    @SerializedName("posters")
    var posters: List<ImageItem> = emptyList()
)

data class ImageItem(
    @SerializedName("file_path")
    var filePath: String? = null,

    @SerializedName("vote_average")
    var voteAverage: Double? = null
)
