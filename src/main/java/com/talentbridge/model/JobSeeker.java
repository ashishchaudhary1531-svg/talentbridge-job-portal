package com.talentbridge.model;
public final class JobSeeker extends User {
    public JobSeeker(String id,String name,String email,String title,String location,String about,boolean active){super(id,name,email,title,location,about,active);}
    @Override public Role getRole(){return Role.JOB_SEEKER;}
    @Override public String getDashboardLabel(){return "Job seeker dashboard";}
}
