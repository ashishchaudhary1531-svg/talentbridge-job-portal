package com.talentbridge.model;

public abstract class User {
    private final String id, name, email, title, location, about;
    private final boolean active;
    protected User(String id,String name,String email,String title,String location,String about,boolean active) {
        this.id=id; this.name=name; this.email=email; this.title=title; this.location=location; this.about=about; this.active=active;
    }
    public String getId(){return id;} public String getName(){return name;} public String getEmail(){return email;}
    public String getTitle(){return title;} public String getLocation(){return location;} public String getAbout(){return about;}
    public boolean isActive(){return active;} public abstract Role getRole(); public abstract String getDashboardLabel();
    public String getRoleName(){return getRole().name();}
}
