package com.talentbridge.model;
public final class ApplicationView {
    private final String id,jobId,seekerId,seekerName,seekerTitle,jobTitle,companyName,status,appliedAt,coverNote,resumeName;
    public ApplicationView(String id,String jobId,String seekerId,String seekerName,String seekerTitle,String jobTitle,String companyName,String status,String appliedAt,String coverNote,String resumeName){
        this.id=id;this.jobId=jobId;this.seekerId=seekerId;this.seekerName=seekerName;this.seekerTitle=seekerTitle;this.jobTitle=jobTitle;this.companyName=companyName;this.status=status;this.appliedAt=appliedAt;this.coverNote=coverNote;this.resumeName=resumeName;
    }
    public String getId(){return id;} public String getJobId(){return jobId;} public String getSeekerId(){return seekerId;}
    public String getSeekerName(){return seekerName;} public String getSeekerTitle(){return seekerTitle;} public String getJobTitle(){return jobTitle;}
    public String getCompanyName(){return companyName;} public String getStatus(){return status;} public String getAppliedAt(){return appliedAt;}
    public String getCoverNote(){return coverNote;} public String getResumeName(){return resumeName;}
}
