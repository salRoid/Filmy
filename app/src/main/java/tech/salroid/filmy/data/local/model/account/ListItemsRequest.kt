package tech.salroid.filmy.data.local.model.account

import com.google.gson.annotations.SerializedName

/** Body for adding to / removing from a TMDB v4 list, which holds both movies and shows. */
data class ListItemsRequest(
    @SerializedName("items")
    var items: List<ListItemRef>,
)

data class ListItemRef(
    @SerializedName("media_type")
    var mediaType: String,

    @SerializedName("media_id")
    var mediaId: Int,
) {
    companion object {
        const val MEDIA_TYPE_MOVIE = "movie"
        const val MEDIA_TYPE_TV = "tv"

        fun mediaTypeOf(isTv: Boolean): String = if (isTv) MEDIA_TYPE_TV else MEDIA_TYPE_MOVIE
    }
}
