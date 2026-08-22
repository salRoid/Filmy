package tech.salroid.filmy.data.local.model.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Body for adding to / removing from a TMDB v4 list, which holds both movies and shows. */
@Serializable
data class ListItemsRequest(
    @SerialName("items")
    var items: List<ListItemRef>,
)

@Serializable
data class ListItemRef(
    @SerialName("media_type")
    var mediaType: String,

    @SerialName("media_id")
    var mediaId: Int,
) {
    companion object {
        const val MEDIA_TYPE_MOVIE = "movie"
        const val MEDIA_TYPE_TV = "tv"

        fun mediaTypeOf(isTv: Boolean): String = if (isTv) MEDIA_TYPE_TV else MEDIA_TYPE_MOVIE
    }
}
