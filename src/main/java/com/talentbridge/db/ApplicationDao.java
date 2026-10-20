package com.talentbridge.db;
import com.talentbridge.model.ApplicationView;
import java.sql.*;
import java.util.*;
public final class ApplicationDao {
    public record ResumeFile(String fileName, String path) {}
    private static final String VIEW="SELECT a.id,a.job_id,a.seeker_id,s.full_name seeker_name,s.title seeker_title,j.title job_title,u.company_name,a.status,a.applied_at,a.cover_note,a.resume_name FROM applications a JOIN users s ON s.id=a.seeker_id JOIN jobs j ON j.id=a.job_id JOIN users u ON u.id=j.employer_id ";
    public String apply(String jobId,String seekerId,String coverNote,String resumeName,String resumePath)throws SQLException{
        String id=UUID.randomUUID().toString();String sql="INSERT INTO applications(id,job_id,seeker_id,status,cover_note,resume_name,resume_path) VALUES(?,?,?,'IN_REVIEW',?,?,?)";
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement(sql)){ps.setString(1,id);ps.setString(2,jobId);ps.setString(3,seekerId);ps.setString(4,coverNote);ps.setString(5,resumeName);ps.setString(6,resumePath);ps.executeUpdate();}return id;
    }
    public List<ApplicationView> findForSeeker(String id)throws SQLException{return query(VIEW+"WHERE a.seeker_id=? ORDER BY a.applied_at DESC",id);}
    public List<ApplicationView> findForEmployer(String id)throws SQLException{return query(VIEW+"WHERE j.employer_id=? ORDER BY a.applied_at DESC",id);}
    public List<ApplicationView> findForJob(String id)throws SQLException{return query(VIEW+"WHERE a.job_id=? ORDER BY a.applied_at DESC",id);}
    public int countAll()throws SQLException{try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("SELECT COUNT(*) FROM applications");ResultSet rs=ps.executeQuery()){return rs.next()?rs.getInt(1):0;}}
    public int countForEmployer(String id)throws SQLException{try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("SELECT COUNT(*) FROM applications a JOIN jobs j ON j.id=a.job_id WHERE j.employer_id=?")){ps.setString(1,id);try(ResultSet rs=ps.executeQuery()){return rs.next()?rs.getInt(1):0;}}}
    public int countForSeeker(String id)throws SQLException{try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("SELECT COUNT(*) FROM applications WHERE seeker_id=?")){ps.setString(1,id);try(ResultSet rs=ps.executeQuery()){return rs.next()?rs.getInt(1):0;}}}
    public Map<String,Integer> statusCountsForEmployer(String employerId)throws SQLException{return statusCounts("SELECT a.status,COUNT(*) FROM applications a JOIN jobs j ON j.id=a.job_id WHERE j.employer_id=? GROUP BY a.status",employerId);}
    public Map<String,Integer> statusCounts()throws SQLException{return statusCounts("SELECT status,COUNT(*) FROM applications GROUP BY status",null);}
    public boolean updateStatus(String appId,String employerId,String status)throws SQLException{
        String sql="UPDATE applications SET status=? WHERE id=? AND EXISTS(SELECT 1 FROM jobs WHERE jobs.id=applications.job_id AND jobs.employer_id=?)";
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement(sql)){ps.setString(1,status);ps.setString(2,appId);ps.setString(3,employerId);return ps.executeUpdate()==1;}
    }
    public boolean isParticipant(String appId,String userId)throws SQLException{
        String sql="SELECT 1 FROM applications a JOIN jobs j ON j.id=a.job_id WHERE a.id=? AND (a.seeker_id=? OR j.employer_id=?)";
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement(sql)){ps.setString(1,appId);ps.setString(2,userId);ps.setString(3,userId);try(ResultSet rs=ps.executeQuery()){return rs.next();}}
    }
    public Optional<ResumeFile> findResumeForParticipant(String appId, String userId) throws SQLException {
        String sql = "SELECT a.resume_name,a.resume_path FROM applications a JOIN jobs j ON j.id=a.job_id WHERE a.id=? AND (a.seeker_id=? OR j.employer_id=?) AND a.resume_path<>''";
        try (Connection c=Database.getConnection(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setString(1, appId); ps.setString(2, userId); ps.setString(3, userId);
            try (ResultSet rs=ps.executeQuery()) { return rs.next() ? Optional.of(new ResumeFile(rs.getString(1),rs.getString(2))) : Optional.empty(); }
        }
    }
    private List<ApplicationView> query(String sql,String value)throws SQLException{
        List<ApplicationView> items=new ArrayList<>();try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement(sql)){ps.setString(1,value);try(ResultSet rs=ps.executeQuery()){while(rs.next())items.add(new ApplicationView(rs.getString("id"),rs.getString("job_id"),rs.getString("seeker_id"),rs.getString("seeker_name"),rs.getString("seeker_title"),rs.getString("job_title"),rs.getString("company_name"),rs.getString("status"),rs.getString("applied_at"),rs.getString("cover_note"),rs.getString("resume_name")));}}return items;
    }
    private Map<String,Integer> statusCounts(String sql,String employerId)throws SQLException{
        Map<String,Integer> counts=new LinkedHashMap<>();
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement(sql)){
            if(employerId!=null)ps.setString(1,employerId);
            try(ResultSet rs=ps.executeQuery()){while(rs.next())counts.put(rs.getString(1),rs.getInt(2));}
        }
        return counts;
    }
}
