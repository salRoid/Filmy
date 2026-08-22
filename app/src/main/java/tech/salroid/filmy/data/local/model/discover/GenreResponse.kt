package tech.salroid.filmy.data.local.model.discover

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import tech.salroid.filmy.data.local.model.Genre

@Serializable
data class GenreResponse(
    @SerialName("genres")
    var genres: List<Genre> = emptyList()
)
