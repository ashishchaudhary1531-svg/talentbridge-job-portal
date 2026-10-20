package com.talentbridge.model;
public final class Employer extends User {
    private final String companyName;
    public Employer(String id,String name,String email,String title,String location,String about,boolean active,String companyName){super(id,name,email,title,location,about,active);this.companyName=companyName;}
    public String getCompanyName(){return companyName;}
    @Override public Role getRole(){return Role.EMPLOYER;}
    @Override public String getDashboardLabel(){return "Employer dashboard";}
}
