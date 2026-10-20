package com.talentbridge.model;
public final class Job {
    private final String id,employerId,title,description,category,location,employmentType,workMode,salary,tags,status,submittedAt,companyName;
    private final int applicantCount;
    public Job(String id,String employerId,String title,String description,String category,String location,String employmentType,String workMode,String salary,String tags,String status,String submittedAt,String companyName,int applicantCount){
        this.id=id;this.employerId=employerId;this.title=title;this.description=description;this.category=category;this.location=location;this.employmentType=employmentType;this.workMode=workMode;this.salary=salary;this.tags=tags;this.status=status;this.submittedAt=submittedAt;this.companyName=companyName;this.applicantCount=applicantCount;
    }
    public String getId(){return id;} public String getEmployerId(){return employerId;} public String getTitle(){return title;}
    public String getDescription(){return description;} public String getCategory(){return category;} public String getLocation(){return location;}
    public String getEmploymentType(){return employmentType;} public String getWorkMode(){return workMode;} public String getSalary(){return salary;}
    public String getTags(){return tags;} public String getStatus(){return status;} public String getSubmittedAt(){return submittedAt;}
    public String getCompanyName(){return companyName;} public int getApplicantCount(){return applicantCount;}
}
