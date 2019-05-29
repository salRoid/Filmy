package tech.salroid.filmy.work_manager;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;


/*
 * Filmy Application for Android
 * Copyright (c) 2016 Ramankit Singh (http://github.com/webianks).
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


public class FilmyWorkManager {

    private static final long SYNC_INTERVAL = 21600000;
    private Context context;

    public FilmyWorkManager(Context context) {
        this.context = context;
    }

    public void createWork() {

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build();

        PeriodicWorkRequest workRequestTrending = new PeriodicWorkRequest.Builder(
                TrendingWorker.class,SYNC_INTERVAL,
                TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build();

        PeriodicWorkRequest workRequestInTheaters = new PeriodicWorkRequest.Builder(
                InTheatersWorker.class,SYNC_INTERVAL,
                TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build();

        PeriodicWorkRequest workRequestUpcoming = new PeriodicWorkRequest.Builder(
                UpcomingWorker.class,SYNC_INTERVAL,
                TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance().enqueueUniquePeriodicWork("filmy-trending-updates",
                ExistingPeriodicWorkPolicy.KEEP,workRequestTrending);

        WorkManager.getInstance().enqueueUniquePeriodicWork("filmy-intheaters-updates",
                ExistingPeriodicWorkPolicy.KEEP,workRequestInTheaters);

        WorkManager.getInstance().enqueueUniquePeriodicWork("filmy-upcoming-updates",
                ExistingPeriodicWorkPolicy.KEEP,workRequestUpcoming);
    }


}