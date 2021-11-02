package tech.salroid.filmy.parser;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tech.salroid.filmy.data_classes.CastMemberDetailsData;
import tech.salroid.filmy.data_classes.CrewMemberDetailsData;
import tech.salroid.filmy.data_classes.SimilarMoviesData;

/*
 * Filmy Application for Android
 * Copyright (c) 2016 Sajal Gupta (http://github.com/salroid).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class MovieDetailsActivityParseWork {

    private Context context;
    private String result;

    public MovieDetailsActivityParseWork(Context context, String result) {
        this.context = context;
        this.result = result;
    }

    public List<CastMemberDetailsData> parseCastMembers() {
        final List<CastMemberDetailsData> allCastMembers = new ArrayList<CastMemberDetailsData>();
        CastMemberDetailsData castMember = null;

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("cast");

            for (int i = 0; i < jsonArray.length(); i++) {
                String id = jsonArray.getJSONObject(i).getString("id");
                String name = jsonArray.getJSONObject(i).getString("name");
                String rolePlayed = jsonArray.getJSONObject(i).getString("character");
                String displayProfile = jsonArray.getJSONObject(i).getString("profile_path");
                displayProfile = "http://image.tmdb.org/t/p/w185" + displayProfile;

                castMember = new CastMemberDetailsData();
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

                if (!memberProfile.contains("null")) {
                    crewMember = new CrewMemberDetailsData();
                    crewMember.setCrewMemberId(memberId);
                    crewMember.setCrewMemberName(memberName);
                    crewMember.setCrewMemberJob(memberJobDescription);
                    crewMember.setCrewDisplayProfile(memberProfile);
                    allCrewMembers.add(crewMember);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return allCrewMembers;
    }

    public List<SimilarMoviesData> parse_similar_movies() {

        final List<SimilarMoviesData> setterGettersimilarArray = new ArrayList<SimilarMoviesData>();
        SimilarMoviesData setterGettersimilar = null;

        try {
            JSONObject jsonObject = new JSONObject(result);

            JSONArray jsonArray = jsonObject.getJSONArray("results");
            ;

            for (int i = 0; i < jsonArray.length(); i++) {

                setterGettersimilar = new SimilarMoviesData();

                String id = (jsonArray.getJSONObject(i)).getString("id");
                String title = (jsonArray.getJSONObject(i)).getString("original_title");
                String movie_poster = "http://image.tmdb.org/t/p/w185" + (jsonArray.getJSONObject(i)).getString("poster_path");

                if (movie_poster.contains("null")) {

                } else {
                    setterGettersimilar.setMovie_id(id);
                    setterGettersimilar.setMovie_banner(movie_poster);
                    setterGettersimilar.setMovie_title(title);

                    setterGettersimilarArray.add(setterGettersimilar);
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return setterGettersimilarArray;
    }

}
