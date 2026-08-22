package tech.salroid.filmy.data.local.model

import android.content.Context
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import tech.salroid.filmy.R

@Serializable
data class AuthorDetails(

    @SerialName("name")
    var name: String? = null,

    @SerialName("username")
    var username: String? = null,

    @SerialName("avatar_path")
    var avatarPath: String? = null,

    @SerialName("rating")
    var rating: Int? = null
) {

    fun getAvatarUrl(context: Context): String {
        var tmdbUrl = context.getString(R.string.member_profile_url, avatarPath)
        avatarPath?.let {
            if (it.contains("www.gravatar.com")) {
                tmdbUrl = it.subSequence(1, it.length - 1).toString()
            }
        }
        return tmdbUrl
    }
}