package tech.salroid.filmy.parser

import org.json.JSONException
import org.json.JSONObject
import tech.salroid.filmy.data.CastMemberDetailsData
import tech.salroid.filmy.data.CrewMemberDetailsData
import tech.salroid.filmy.data.SimilarMoviesData
import java.util.*

class MovieDetailsActivityParseWork(private val result: String) {

    fun parseCastMembers(): List<CastMemberDetailsData> {
        val allCastMembers: MutableList<CastMemberDetailsData> = ArrayList()
        var castMember: CastMemberDetailsData

        try {
            val jsonObject = JSONObject(result)
            val jsonArray = jsonObject.getJSONArray("cast")

            for (i in 0 until jsonArray.length()) {
                val id = jsonArray.getJSONObject(i).getString("id")
                val name = jsonArray.getJSONObject(i).getString("name")
                val rolePlayed = jsonArray.getJSONObject(i).getString("character")
                var displayProfile = jsonArray.getJSONObject(i).getString("profile_path")
                displayProfile = "http://image.tmdb.org/t/p/w185$displayProfile"

                castMember = CastMemberDetailsData(id)
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

    fun parseCrewMembers(): List<CrewMemberDetailsData> {
        val allCrewMembers: MutableList<CrewMemberDetailsData> = ArrayList()
        var crewMember: CrewMemberDetailsData

        try {
            val jsonObject = JSONObject(result)
            val crewArray = jsonObject.getJSONArray("crew")

            for (i in 0 until crewArray.length()) {
                val memberId = crewArray.getJSONObject(i).getString("id")
                val memberJobDescription = crewArray.getJSONObject(i).getString("job")
                val memberName = crewArray.getJSONObject(i).getString("name")
                var memberProfile = crewArray.getJSONObject(i).getString("profile_path")
                memberProfile = "http://image.tmdb.org/t/p/w185$memberProfile"
                crewMember = CrewMemberDetailsData(memberId)

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

    fun parseSimilarMovies(): List<SimilarMoviesData> {
        val similarArray: MutableList<SimilarMoviesData> = ArrayList()
        var similar: SimilarMoviesData

        try {
            val jsonObject = JSONObject(result)
            val jsonArray = jsonObject.getJSONArray("results")

            for (i in 0 until jsonArray.length()) {
                val id = jsonArray.getJSONObject(i).getString("id")
                val title = jsonArray.getJSONObject(i).getString("original_title")
                val poster = "http://image.tmdb.org/t/p/w185" + jsonArray.getJSONObject(i)
                    .getString("poster_path")

                similar = SimilarMoviesData(id)

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
}