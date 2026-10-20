package com.talentbridge.model;
public final class Administrator extends User {
    public Administrator(String id,String name,String email,String title,String location,String about,boolean active){super(id,name,email,title,location,about,active);}
    @Override public Role getRole(){return Role.ADMIN;}
    @Override public String getDashboardLabel(){return "Administrator dashboard";}
}
