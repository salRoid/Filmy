package tech.salroid.filmy.data.local.db.entity

import com.google.gson.annotations.SerializedName

data class Gravatar(
    @SerializedName("hash")
    var hash: String? = null
) {
    fun getCompleteUrl() = "https://www.gravatar.com/avatar/$hash.jpg?s=500"
}