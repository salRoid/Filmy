package tech.salroid.filmy.data.local.db.entity

import com.google.gson.annotations.SerializedName

data class Tmdb(
    @SerializedName("avatar_path")
    var avatarPath: String? = null
)