package tech.salroid.filmy.data_classes;

/**
 * Created by salroid on 11/1/2016.
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
