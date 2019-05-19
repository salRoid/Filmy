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

public class CastDetailsData {

   private String castName, castCharacter, castDisplayProfile, castId;

    public void setCastName(String castName) {
        this.castName = castName;
    }

    public String getCastName() {
        return castName;
    }

    public void setCastId(String castId) {
        this.castId = castId;
    }

    public String getCastId() {
        return castId;
    }

    public void setCastDisplayProfile(String castDisplayProfile) {
        this.castDisplayProfile = castDisplayProfile;
    }

    public String getCastDisplayProfile() {
        return castDisplayProfile;
    }

    public void setCastCharacter(String castCharacter) {
        this.castCharacter = castCharacter;
    }

    public String getCastCharacter() {
        return castCharacter;
    }
}