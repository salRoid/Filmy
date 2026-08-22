package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieKeywordsResponse(
    @SerialName("keywords")
    var keywords: List<Keyword> = emptyList()
)
