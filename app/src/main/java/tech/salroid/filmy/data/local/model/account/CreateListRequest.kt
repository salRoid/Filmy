package tech.salroid.filmy.data.local.model.account

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class CreateListRequest(
    @SerialName("name")
    var name: String,

    @SerialName("description")
    var description: String = "",

    // TMDB v4 requires the language, and kotlinx leaves out values that equal
    // their default unless told otherwise.
    @EncodeDefault
    @SerialName("iso_639_1")
    var language: String = "en",
)
