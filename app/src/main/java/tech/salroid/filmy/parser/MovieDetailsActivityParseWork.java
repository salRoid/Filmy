package tech.salroid.filmy.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import tech.salroid.filmy.data.CastMemberDetailsData;
import tech.salroid.filmy.data.CrewMemberDetailsData;
import tech.salroid.filmy.data.SimilarMoviesData;

public class MovieDetailsActivityParseWork {

    private final String result;

    public MovieDetailsActivityParseWork(String result) {
        this.result = result;
    }

    public List<CastMemberDetailsData> parseCastMembers() {
        final List<CastMemberDetailsData> allCastMembers = new ArrayList<>();
        CastMemberDetailsData castMember;

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("cast");

            for (int i = 0; i < jsonArray.length(); i++) {
                String id = jsonArray.getJSONObject(i).getString("id");
                String name = jsonArray.getJSONObject(i).getString("name");
                String rolePlayed = jsonArray.getJSONObject(i).getString("character");
                String displayProfile = jsonArray.getJSONObject(i).getString("profile_path");
                displayProfile = "http://image.tmdb.org/t/p/w185" + displayProfile;

                castMember = new CastMemberDetailsData(id);
                castMember.setCastId(id);
                castMember.setCastName(name);
                castMember.setCastRolePlayed(rolePlayed);
                castMember.setCastDisplayProfile(displayProfile);

                allCastMembers.add(castMember);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return allCastMembers;
    }

    public List<CrewMemberDetailsData> parseCrewMembers() {
        final List<CrewMemberDetailsData> allCrewMembers = new ArrayList<CrewMemberDetailsData>();
        CrewMemberDetailsData crewMember;

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray crewArray = jsonObject.getJSONArray("crew");

            for (int i = 0; i < crewArray.length(); i++) {

                String memberId = crewArray.getJSONObject(i).getString("id");
                String memberJobDescription = crewArray.getJSONObject(i).getString("job");
                String memberName = crewArray.getJSONObject(i).getString("name");
                String memberProfile = crewArray.getJSONObject(i).getString("profile_path");
                memberProfile = "http://image.tmdb.org/t/p/w185" + memberProfile;

                crewMember = new CrewMemberDetailsData(memberId);

                if (!memberProfile.contains("null")) {
                    crewMember.setCrewMemberId(memberId);
                    crewMember.setCrewMemberName(memberName);
                    crewMember.setCrewMemberJob(memberJobDescription);
                    crewMember.setCrewMemberProfile(memberProfile);
                    allCrewMembers.add(crewMember);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return allCrewMembers;
    }

    public List<SimilarMoviesData> parse_similar_movies() {

        final List<SimilarMoviesData> similarArray = new ArrayList<>();
        SimilarMoviesData similar;

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {

                String id = (jsonArray.getJSONObject(i)).getString("id");
                String title = (jsonArray.getJSONObject(i)).getString("original_title");
                String poster = "http://image.tmdb.org/t/p/w185" + (jsonArray.getJSONObject(i)).getString("poster_path");

                similar = new SimilarMoviesData(id);

                if (!poster.contains("null")) {
                    similar.setId(id);
                    similar.setBanner(poster);
                    similar.setTitle(title);

                    similarArray.add(similar);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return similarArray;
    }
}
