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

    String crew_name, crew_job, crew_profile, crew_id;

    public void setCrew_id(String crew_id) {
        this.crew_id = crew_id;
    }

    public void setCrew_job(String crew_job) {
        this.crew_job = crew_job;
    }

    public void setCrew_name(String crew_name) {
        this.crew_name = crew_name;
    }

    public void setCrew_profile(String crew_profile) {
        this.crew_profile = crew_profile;
    }

    public String getCrew_id() {
        return crew_id;
    }

    public String getCrew_job() {
        return crew_job;
    }

    public String getCrew_profile() {
        return crew_profile;
    }

    public String getCrew_name() {
        return crew_name;
    }
}
