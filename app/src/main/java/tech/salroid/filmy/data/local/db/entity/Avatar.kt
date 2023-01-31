package tech.salroid.filmy.data.local.db.entity

import com.google.gson.annotations.SerializedName

data class Avatar(
    @SerializedName("gravatar")
    var gravatar: Gravatar? = Gravatar(),

    @SerializedName("tmdb")
    var tmdb: Tmdb? = Tmdb()
)