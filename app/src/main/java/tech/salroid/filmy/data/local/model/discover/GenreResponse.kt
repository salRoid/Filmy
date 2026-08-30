package tech.salroid.filmy.data.local.model.discover

import com.google.gson.annotations.SerializedName
import tech.salroid.filmy.data.local.model.Genre

data class GenreResponse(
    @SerializedName("genres")
    var genres: List<Genre> = emptyList()
)
