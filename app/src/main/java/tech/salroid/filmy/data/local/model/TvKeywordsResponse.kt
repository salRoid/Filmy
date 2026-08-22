package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TvKeywordsResponse(
    @SerialName("results")
    var results: List<Keyword> = emptyList()
)
