package tech.salroid.filmy.data.local.db

import androidx.room.TypeConverter
import com.google.gson.Gson

import com.google.gson.reflect.TypeToken
import tech.salroid.filmy.data.local.model.*
import tech.salroid.filmy.data.local.model.Collection
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
    fun fromStringOfArrayListOfGenres(value: String?): ArrayList<Genres> {
        if (value.isNullOrEmpty()) return arrayListOf()
        val listType: Type = object : TypeToken<ArrayList<Genres>>() {}.type
        return Gson().fromJson(value, listType) ?: arrayListOf()
    }

    @TypeConverter
    fun fromArrayListOfGenres(genres: ArrayList<Genres>): String {
        return Gson().toJson(genres)
    }

    @TypeConverter
    fun fromStringOfArrayListOfProductionCompanies(value: String?): ArrayList<ProductionCompanies> {
        if (value.isNullOrEmpty()) return arrayListOf()
        val listType: Type = object : TypeToken<ArrayList<ProductionCompanies>>() {}.type
        return Gson().fromJson(value, listType) ?: arrayListOf()
    }

    @TypeConverter
    fun fromArrayListOfProductionCompanies(genres: ArrayList<ProductionCompanies>): String {
        return Gson().toJson(genres)
    }

    @TypeConverter
    fun fromStringOfArrayListOfProductionCountries(value: String?): ArrayList<ProductionCountries> {
        if (value.isNullOrEmpty()) return arrayListOf()
        val listType: Type = object : TypeToken<ArrayList<ProductionCountries>>() {}.type
        return Gson().fromJson(value, listType) ?: arrayListOf()
    }

    @TypeConverter
    fun fromArrayListOfProductionCountries(genres: ArrayList<ProductionCountries>): String {
        return Gson().toJson(genres)
    }

    @TypeConverter
    fun fromStringOfArrayListOfSpokenLanguages(value: String?): ArrayList<SpokenLanguages> {
        if (value.isNullOrEmpty()) return arrayListOf()
        val listType: Type = object : TypeToken<ArrayList<SpokenLanguages>>() {}.type
        return Gson().fromJson(value, listType) ?: arrayListOf()
    }

    @TypeConverter
    fun fromArrayListOfSpokenLanguages(genres: ArrayList<SpokenLanguages>): String {
        return Gson().toJson(genres)
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