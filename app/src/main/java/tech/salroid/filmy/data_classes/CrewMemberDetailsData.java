package tech.salroid.filmy.data_classes;

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

public class CrewMemberDetailsData {

    private String crewMemberId, crewMemberName, crewMemberJob, crewMemberProfile;

    public void setCrewMemberId(String crewMemberId) {
        this.crewMemberId = crewMemberId;
    }

    public String getCrewMemberId() {
        return crewMemberId;
    }

    public void setCrewMemberName(String crewMemberName) {
        this.crewMemberName = crewMemberName;
    }

    public String getCrewMemberName() {
        return crewMemberName;
    }

    public void setCrewMemberJob(String crewMemberJob) {
        this.crewMemberJob = crewMemberJob;
    }

    public String getCrewMemberJob() {
        return crewMemberJob;
    }

    public void setCrewDisplayProfile(String crewMemberProfile) {
        this.crewMemberProfile = crewMemberProfile;
    }

    public String getCrewMemberProfile() {
        return crewMemberProfile;
    }
}
