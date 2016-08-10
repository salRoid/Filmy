package tech.salroid.filmy.services;

import android.content.ComponentName;
import android.content.Context;

import me.tatarka.support.job.JobInfo;
import me.tatarka.support.job.JobScheduler;

/**
 * Created by R Ankit on 05-08-2016.
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