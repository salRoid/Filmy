package tech.salroid.filmy.data.local.db.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Gravatar(
    @SerialName("hash")
    var hash: String? = null
) {
    fun getCompleteUrl() = "https://www.gravatar.com/avatar/$hash.jpg?s=500"
}