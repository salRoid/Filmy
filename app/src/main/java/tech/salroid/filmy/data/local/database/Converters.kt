package tech.salroid.filmy.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson

import com.google.gson.reflect.TypeToken
import tech.salroid.filmy.data.local.*
import tech.salroid.filmy.data.local.Collection
import java.lang.reflect.Type

class Converters {

    @TypeConverter
    fun fromString(value: String?): ArrayList<Int>? {
        val listType: Type = object : TypeToken<ArrayList<Int>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: ArrayList<Int>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromStringOfCollection(value: String?): Collection? {
        val listType: Type = object : TypeToken<Collection?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromCollection(collection: Collection?): String? {
        val gson = Gson()
        return gson.toJson(collection)
    }

    @TypeConverter
    fun fromStringOfArrayListOfGenres(value: String?): ArrayList<Genres?>? {
        val listType: Type = object : TypeToken<ArrayList<Genres?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayListOfGenres(genres: ArrayList<Genres?>?): String? {
        val gson = Gson()
        return gson.toJson(genres)
    }

    @TypeConverter
    fun fromStringOfArrayListOfProductionCompanies(value: String?): ArrayList<ProductionCompanies?>? {
        val listType: Type = object : TypeToken<ArrayList<ProductionCompanies?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayListOfProductionCompanies(genres: ArrayList<ProductionCompanies?>?): String? {
        val gson = Gson()
        return gson.toJson(genres)
    }

    @TypeConverter
    fun fromStringOfArrayListOfProductionCountries(value: String?): ArrayList<ProductionCountries?>? {
        val listType: Type = object : TypeToken<ArrayList<ProductionCountries?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayListOfProductionCountries(genres: ArrayList<ProductionCountries?>?): String? {
        val gson = Gson()
        return gson.toJson(genres)
    }

    @TypeConverter
    fun fromStringOfArrayListOfSpokenLanguages(value: String?): ArrayList<SpokenLanguages?>? {
        val listType: Type = object : TypeToken<ArrayList<SpokenLanguages?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayListOfSpokenLanguages(genres: ArrayList<SpokenLanguages?>?): String? {
        val gson = Gson()
        return gson.toJson(genres)
    }

    @TypeConverter
    fun fromStringOfTrailers(value: String?): Trailers? {
        val listType: Type = object : TypeToken<Trailers?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromTrailers(genres: Trailers?): String? {
        val gson = Gson()
        return gson.toJson(genres)
    }
}