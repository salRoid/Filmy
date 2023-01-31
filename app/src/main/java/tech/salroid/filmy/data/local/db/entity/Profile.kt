package tech.salroid.filmy.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "profile")
data class Profile(

    @SerializedName("avatar")
    var avatar: Avatar? = Avatar(),

    @PrimaryKey
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("iso_639_1")
    var iso6391: String? = null,

    @SerializedName("iso_3166_1")
    var iso31661: String? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("include_adult")
    var includeAdult: Boolean? = null,

    @SerializedName("username")
    var username: String? = null
)