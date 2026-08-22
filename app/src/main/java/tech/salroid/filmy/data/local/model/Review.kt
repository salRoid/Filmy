package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Review(

    @SerialName("author")
    var author: String? = null,

    @SerialName("author_details")
    var authorDetails: AuthorDetails? = null,

    @SerialName("content")
    var content: String? = null,

    @SerialName("created_at")
    var createdAt: String? = null,

    @SerialName("id")
    var id: String? = null,

    @SerialName("updated_at")
    var updatedAt: String? = null,

    @SerialName("url")
    var url: String? = null
)