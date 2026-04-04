package tech.salroid.filmy.data.local.model

import com.google.gson.annotations.SerializedName

data class Videos(
    @SerializedName("results")
    val results: List<VideoResult> = emptyList()
)

data class VideoResult(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("key")
    val key: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("site")
    val site: String? = null,
    @SerializedName("size")
    val size: Int? = null,
    @SerializedName("type")
    val type: String? = null
)
