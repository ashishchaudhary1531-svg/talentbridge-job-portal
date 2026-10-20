package com.talentbridge.db;
import com.talentbridge.model.Job;
import com.talentbridge.model.Repository;
import java.sql.*;
import java.util.*;
public final class JobDao implements Repository<Job,String> {
    private static final String SELECT="SELECT j.*,u.company_name,(SELECT COUNT(*) FROM applications a WHERE a.job_id=j.id) applicant_count FROM jobs j JOIN users u ON u.id=j.employer_id ";
    public List<Job> searchApproved(String query,String location,String category)throws SQLException{
        StringBuilder sql=new StringBuilder(SELECT).append("WHERE j.status='APPROVED'");List<String> params=new ArrayList<>();
        if(query!=null&&!query.isBlank()){sql.append(" AND (j.title LIKE ? OR j.description LIKE ? OR j.tags LIKE ? OR u.company_name LIKE ?)");String like="%"+query.trim()+"%";params.add(like);params.add(like);params.add(like);params.add(like);}
        if(location!=null&&!location.isBlank()){sql.append(" AND j.location LIKE ?");params.add("%"+location.trim()+"%");}
        if(category!=null&&!category.isBlank()){sql.append(" AND j.category=?");params.add(category.trim());}
        sql.append(" ORDER BY j.submitted_at DESC LIMIT 100");
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement(sql.toString())){for(int i=0;i<params.size();i++)ps.setString(i+1,params.get(i));try(ResultSet rs=ps.executeQuery()){return collect(rs);}}
    }
    public List<Job> findForEmployer(String employerId)throws SQLException{
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement(SELECT+"WHERE j.employer_id=? ORDER BY j.submitted_at DESC")){ps.setString(1,employerId);try(ResultSet rs=ps.executeQuery()){return collect(rs);}}
    }
    public List<Job> findPending()throws SQLException{
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement(SELECT+"WHERE j.status='PENDING' ORDER BY j.submitted_at ASC");ResultSet rs=ps.executeQuery()){return collect(rs);}
    }
    @Override public Optional<Job> findById(String id)throws SQLException{
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement(SELECT+"WHERE j.id=?")){ps.setString(1,id);try(ResultSet rs=ps.executeQuery()){return rs.next()?Optional.of(map(rs)):Optional.empty();}}
    }
    @Override public List<Job> findAll()throws SQLException{
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement(SELECT+"ORDER BY j.submitted_at DESC");ResultSet rs=ps.executeQuery()){return collect(rs);}
    }
    public int countByStatus(String status)throws SQLException{try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("SELECT COUNT(*) FROM jobs WHERE status=?")){ps.setString(1,status);try(ResultSet rs=ps.executeQuery()){return rs.next()?rs.getInt(1):0;}}}
    public int countAll()throws SQLException{try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("SELECT COUNT(*) FROM jobs");ResultSet rs=ps.executeQuery()){return rs.next()?rs.getInt(1):0;}}
    public int countForEmployer(String employerId)throws SQLException{try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("SELECT COUNT(*) FROM jobs WHERE employer_id=?")){ps.setString(1,employerId);try(ResultSet rs=ps.executeQuery()){return rs.next()?rs.getInt(1):0;}}}
    public String create(String employerId,String title,String description,String category,String location,String type,String workMode,String salary,String tags)throws SQLException{
        String id=UUID.randomUUID().toString();String sql="INSERT INTO jobs(id,employer_id,title,description,category,location,employment_type,work_mode,salary,tags,status,expires_at) VALUES(?,?,?,?,?,?,?,?,?,?,'PENDING',date('now','+60 day'))";
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement(sql)){ps.setString(1,id);ps.setString(2,employerId);ps.setString(3,title);ps.setString(4,description);ps.setString(5,category);ps.setString(6,location);ps.setString(7,type);ps.setString(8,workMode);ps.setString(9,salary);ps.setString(10,tags);ps.executeUpdate();}return id;
    }
    public boolean update(String id,String employerId,String title,String description,String category,String location,String type,String workMode,String salary,String tags)throws SQLException{
        String sql="UPDATE jobs SET title=?,description=?,category=?,location=?,employment_type=?,work_mode=?,salary=?,tags=?,status='PENDING',submitted_at=CURRENT_TIMESTAMP WHERE id=? AND employer_id=?";
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement(sql)){ps.setString(1,title);ps.setString(2,description);ps.setString(3,category);ps.setString(4,location);ps.setString(5,type);ps.setString(6,workMode);ps.setString(7,salary);ps.setString(8,tags);ps.setString(9,id);ps.setString(10,employerId);return ps.executeUpdate()==1;}
    }
    public boolean updateStatus(String id,String status)throws SQLException{try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("UPDATE jobs SET status=? WHERE id=?")){ps.setString(1,status);ps.setString(2,id);return ps.executeUpdate()==1;}}
    public boolean updateStatusForEmployer(String id,String employerId,String status)throws SQLException{try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("UPDATE jobs SET status=? WHERE id=? AND employer_id=?")){ps.setString(1,status);ps.setString(2,id);ps.setString(3,employerId);return ps.executeUpdate()==1;}}
    public boolean deleteForEmployer(String id,String employerId)throws SQLException{try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("DELETE FROM jobs WHERE id=? AND employer_id=?")){ps.setString(1,id);ps.setString(2,employerId);return ps.executeUpdate()==1;}}
    public int closeExpiredJobs()throws SQLException{try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("UPDATE jobs SET status='CLOSED' WHERE status='APPROVED' AND expires_at IS NOT NULL AND expires_at<date('now')")){return ps.executeUpdate();}}
    private List<Job> collect(ResultSet rs)throws SQLException{List<Job> jobs=new ArrayList<>();while(rs.next())jobs.add(map(rs));return jobs;}
    private Job map(ResultSet rs)throws SQLException{return new Job(rs.getString("id"),rs.getString("employer_id"),rs.getString("title"),rs.getString("description"),rs.getString("category"),rs.getString("location"),rs.getString("employment_type"),rs.getString("work_mode"),rs.getString("salary"),rs.getString("tags"),rs.getString("status"),rs.getString("submitted_at"),rs.getString("company_name"),rs.getInt("applicant_count"));}
}
