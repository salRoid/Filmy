package tech.salroid.filmy.data.local.db

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import tech.salroid.filmy.data.local.db.entity.Avatar
import tech.salroid.filmy.data.local.model.*
import tech.salroid.filmy.data.local.model.Collection

class Converters {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @TypeConverter
    fun fromString(value: String?): ArrayList<Int>? {
        if (value.isNullOrEmpty()) return null
        return ArrayList(json.decodeFromString<List<Int>>(value))
    }

    @TypeConverter
    fun fromArrayList(list: ArrayList<Int>?): String? {
        if (list == null) return null
        return json.encodeToString(list.toList())
    }

    @TypeConverter
    fun fromStringOfCollection(value: String?): Collection? {
        if (value.isNullOrEmpty()) return null
        return json.decodeFromString<Collection>(value)
    }

    @TypeConverter
    fun fromCollection(collection: Collection?): String? {
        if (collection == null) return null
        return json.encodeToString(collection)
    }

    @TypeConverter
    fun fromStringOfArrayListOfGenres(value: String?): ArrayList<Genre> {
        if (value.isNullOrEmpty()) return arrayListOf()
        return ArrayList(json.decodeFromString<List<Genre>>(value))
    }

    @TypeConverter
    fun fromArrayListOfGenres(genres: ArrayList<Genre>): String {
        return json.encodeToString(genres.toList())
    }

    @TypeConverter
    fun fromStringOfArrayListOfProductionCompanies(value: String?): ArrayList<ProductionCompanies> {
        if (value.isNullOrEmpty()) return arrayListOf()
        return ArrayList(json.decodeFromString<List<ProductionCompanies>>(value))
    }

    @TypeConverter
    fun fromArrayListOfProductionCompanies(genres: ArrayList<ProductionCompanies>): String {
        return json.encodeToString(genres.toList())
    }

    @TypeConverter
    fun fromStringOfArrayListOfProductionCountries(value: String?): ArrayList<ProductionCountries> {
        if (value.isNullOrEmpty()) return arrayListOf()
        return ArrayList(json.decodeFromString<List<ProductionCountries>>(value))
    }

    @TypeConverter
    fun fromArrayListOfProductionCountries(genres: ArrayList<ProductionCountries>): String {
        return json.encodeToString(genres.toList())
    }

    @TypeConverter
    fun fromStringOfArrayListOfSpokenLanguages(value: String?): ArrayList<SpokenLanguages> {
        if (value.isNullOrEmpty()) return arrayListOf()
        return ArrayList(json.decodeFromString<List<SpokenLanguages>>(value))
    }

    @TypeConverter
    fun fromArrayListOfSpokenLanguages(genres: ArrayList<SpokenLanguages>): String {
        return json.encodeToString(genres.toList())
    }

    @TypeConverter
    fun fromStringOfTrailers(value: String?): Trailers? {
        if (value.isNullOrEmpty()) return null
        return json.decodeFromString<Trailers>(value)
    }

    @TypeConverter
    fun fromTrailers(genres: Trailers?): String? {
        if (genres == null) return null
        return json.encodeToString(genres)
    }

    @TypeConverter
    fun fromStringOfAvatar(value: String?): Avatar? {
        if (value.isNullOrEmpty()) return null
        return json.decodeFromString<Avatar>(value)
    }

    @TypeConverter
    fun fromAvatar(avatar: Avatar?): String? {
        if (avatar == null) return null
        return json.encodeToString(avatar)
    }

    @TypeConverter
    fun fromStringOfVideos(value: String?): Videos? {
        if (value.isNullOrEmpty()) return null
        return json.decodeFromString<Videos>(value)
    }

    @TypeConverter
    fun fromVideos(videos: Videos?): String? {
        if (videos == null) return null
        return json.encodeToString(videos)
    }
}
