package tech.salroid.filmy.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "profile")
@Serializable
data class Profile(

    @SerialName("avatar")
    var avatar: Avatar? = null,

    @PrimaryKey
    @SerialName("id")
    var id: Int? = null,

    @SerialName("iso_639_1")
    var iso6391: String? = null,

    @SerialName("iso_3166_1")
    var iso31661: String? = null,

    @SerialName("name")
    var name: String? = null,

    @SerialName("include_adult")
    var includeAdult: Boolean? = null,

    @SerialName("username")
    var username: String? = null
)