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

public class CrewDetailsData {

    private String crewName, crewJobDescr, crewDisplayProfile, crewId;

    public void setCrewId(String crewId) { this.crewId = crewId; }

    public String getCrewId() {
        return crewId;
    }

    public void setCrewJobDescr(String crewJobDescr) {
        this.crewJobDescr = crewJobDescr;
    }

    public String getCrewJobDescr() {
        return crewJobDescr;
    }

    public void setCrewName(String crewName) {
        this.crewName = crewName;
    }

    public String getCrewName() {
        return crewName;
    }

    public void setCrewDisplayProfile(String crewDisplayProfile) {
        this.crewDisplayProfile = crewDisplayProfile;
    }

    public String getCrewDisplayProfile() {
        return crewDisplayProfile;
    }
}
