package tech.salroid.filmy.data.local.model

import android.content.Context
import com.google.gson.annotations.SerializedName
import tech.salroid.filmy.R

data class AuthorDetails(

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("username")
    var username: String? = null,

    @SerializedName("avatar_path")
    var avatarPath: String? = null,

    @SerializedName("rating")
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