package tech.salroid.filmy.data.local.model.account

import com.google.gson.annotations.SerializedName

/**
 * TMDB v4 answers item changes with HTTP 200 even when an individual item was
 * rejected (e.g. it was already in the list), reporting that per item instead.
 */
data class ListItemsResponse(
    @SerializedName("success")
    var success: Boolean? = null,

    @SerializedName("results")
    var results: List<ListItemResult> = emptyList(),
) {
    val allSucceeded: Boolean get() = success != false && results.all { it.success != false }
}

data class ListItemResult(
    @SerializedName("media_id")
    var mediaId: Int? = null,

    @SerializedName("media_type")
    var mediaType: String? = null,

    @SerializedName("success")
    var success: Boolean? = null,
)
