package tech.salroid.filmy.services;

import android.content.ComponentName;
import android.content.Context;

import me.tatarka.support.job.JobInfo;
import me.tatarka.support.job.JobScheduler;

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


public class FilmyJobScheduler {


    public static final int SYNC_INTERVAL = 60 * 180;
    private JobScheduler jobScheduler;
    private int JOB_ID = 456;
    private int JOB_ID_IMMEDIATE = 654;
    private Context context;

    public FilmyJobScheduler(Context context) {


        this.context = context;
        jobScheduler = JobScheduler.getInstance(context);

    }

    public void createJob() {

        syncImmediately();

        JobInfo.Builder jobBuilder = new JobInfo.Builder(JOB_ID, new ComponentName(context, FilmyJobService.class));

        //PersistableBundle persistableBundle = new PersistableBundle();

        jobBuilder.setPeriodic(SYNC_INTERVAL)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true);

        jobScheduler.schedule(jobBuilder.build());

    }

    private void syncImmediately() {

        JobInfo.Builder jobBuilder = new JobInfo.Builder(JOB_ID_IMMEDIATE, new ComponentName(context, FilmyJobService.class));
        jobBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true);
        jobScheduler.schedule(jobBuilder.build());

    }


}