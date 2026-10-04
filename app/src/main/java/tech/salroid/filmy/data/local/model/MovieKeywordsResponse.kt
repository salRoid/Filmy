package tech.salroid.filmy.data.local.model

import com.google.gson.annotations.SerializedName

data class MovieKeywordsResponse(
    @SerializedName("keywords")
    var keywords: List<Keyword> = emptyList()
)
