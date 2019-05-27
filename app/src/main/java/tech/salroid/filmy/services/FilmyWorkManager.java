package tech.salroid.filmy.services;

import android.content.Context;

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

        /*JobInfo.Builder jobBuilder = new JobInfo.Builder(JOB_ID, new ComponentName(context, FilmyWorker.class));
        //PersistableBundle persistableBundle = new PersistableBundle();
        jobBuilder.setPeriodic(SYNC_INTERVAL)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true);
        jobScheduler.schedule(jobBuilder.build());*/

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(FilmyWorker.class,SYNC_INTERVAL, TimeUnit.MILLISECONDS)
                .build();
        WorkManager.getInstance().enqueue(workRequest);
    }


}