package tech.salroid.filmy.data.local.db.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tmdb(
    @SerialName("avatar_path")
    var avatarPath: String? = null
)