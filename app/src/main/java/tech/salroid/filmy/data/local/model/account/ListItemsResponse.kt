package tech.salroid.filmy.data.local.model.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * TMDB v4 answers item changes with HTTP 200 even when an individual item was
 * rejected (e.g. it was already in the list), reporting that per item instead.
 */
@Serializable
data class ListItemsResponse(
    @SerialName("success")
    var success: Boolean? = null,

    @SerialName("results")
    var results: List<ListItemResult> = emptyList(),
) {
    val allSucceeded: Boolean get() = success != false && results.all { it.success != false }
}

@Serializable
data class ListItemResult(
    @SerialName("media_id")
    var mediaId: Int? = null,

    @SerialName("media_type")
    var mediaType: String? = null,

    @SerialName("success")
    var success: Boolean? = null,
)
