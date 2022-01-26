/*
package tech.salroid.filmy.parser

import org.json.JSONException
import org.json.JSONObject
import tech.salroid.filmy.data.Cast
import tech.salroid.filmy.data.Crew
import tech.salroid.filmy.data.SimilarMovie
import java.util.*

class MovieDetailsActivityParseWork(private val result: String) {

    fun parseCastMembers(): List<Cast> {
        val allCastMembers: MutableList<Cast> = ArrayList()
        var castMember: Cast

        try {
            val jsonObject = JSONObject(result)
            val jsonArray = jsonObject.getJSONArray("cast")

            for (i in 0 until jsonArray.length()) {
                val id = jsonArray.getJSONObject(i).getString("id")
                val name = jsonArray.getJSONObject(i).getString("name")
                val rolePlayed = jsonArray.getJSONObject(i).getString("character")
                var displayProfile = jsonArray.getJSONObject(i).getString("profile_path")
                displayProfile = "http://image.tmdb.org/t/p/w185$displayProfile"

                castMember = Cast(id)
                castMember.castId = id
                castMember.castName = name
                castMember.castRolePlayed = rolePlayed
                castMember.castDisplayProfile = displayProfile

                allCastMembers.add(castMember)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return allCastMembers
    }

    fun parseCrewMembers(): List<Crew> {
        val allCrewMembers: MutableList<Crew> = ArrayList()
        var crewMember: Crew

        try {
            val jsonObject = JSONObject(result)
            val crewArray = jsonObject.getJSONArray("crew")

            for (i in 0 until crewArray.length()) {
                val memberId = crewArray.getJSONObject(i).getString("id")
                val memberJobDescription = crewArray.getJSONObject(i).getString("job")
                val memberName = crewArray.getJSONObject(i).getString("name")
                var memberProfile = crewArray.getJSONObject(i).getString("profile_path")
                memberProfile = "http://image.tmdb.org/t/p/w185$memberProfile"
                crewMember = Crew(memberId)

                if (!memberProfile.contains("null")) {
                    crewMember.crewMemberId = memberId
                    crewMember.crewMemberName = memberName
                    crewMember.crewMemberJob = memberJobDescription
                    crewMember.crewMemberProfile = memberProfile
                    allCrewMembers.add(crewMember)
                }
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return allCrewMembers
    }

    fun parseSimilarMovies(): List<SimilarMovie> {
        val similarArray: MutableList<SimilarMovie> = ArrayList()
        var similar: SimilarMovie

        try {
            val jsonObject = JSONObject(result)
            val jsonArray = jsonObject.getJSONArray("results")

            for (i in 0 until jsonArray.length()) {
                val id = jsonArray.getJSONObject(i).getString("id")
                val title = jsonArray.getJSONObject(i).getString("original_title")
                val poster = "http://image.tmdb.org/t/p/w185" + jsonArray.getJSONObject(i)
                    .getString("poster_path")

                similar = SimilarMovie(id)

                if (!poster.contains("null")) {
                    similar.id = id
                    similar.banner = poster
                    similar.title = title
                    similarArray.add(similar)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return similarArray
    }
}*/
