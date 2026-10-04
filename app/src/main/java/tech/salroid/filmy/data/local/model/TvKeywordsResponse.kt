package tech.salroid.filmy.data.local.model

import com.google.gson.annotations.SerializedName

data class TvKeywordsResponse(
    @SerializedName("results")
    var results: List<Keyword> = emptyList()
)
