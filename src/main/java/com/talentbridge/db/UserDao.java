package com.talentbridge.db;
import com.talentbridge.model.*;
import java.sql.*;
import java.util.*;
public final class UserDao implements Repository<User,String> {
    public record Credentials(User user,String passwordHash){}
    public Optional<Credentials> findCredentials(String email)throws SQLException{
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("SELECT * FROM users WHERE email=?")){
            ps.setString(1,email.trim());try(ResultSet rs=ps.executeQuery()){return rs.next()?Optional.of(new Credentials(map(rs),rs.getString("password_hash"))):Optional.empty();}
        }
    }
    @Override public Optional<User> findById(String id)throws SQLException{
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("SELECT * FROM users WHERE id=?")){ps.setString(1,id);try(ResultSet rs=ps.executeQuery()){return rs.next()?Optional.of(map(rs)):Optional.empty();}}
    }
    @Override public List<User> findAll()throws SQLException{
        List<User> users=new ArrayList<>();try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("SELECT * FROM users ORDER BY created_at DESC");ResultSet rs=ps.executeQuery()){while(rs.next())users.add(map(rs));}return users;
    }
    public int count()throws SQLException{try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("SELECT COUNT(*) FROM users");ResultSet rs=ps.executeQuery()){return rs.next()?rs.getInt(1):0;}}
    public void insert(String id,String name,String email,String hash,Role role,String title,String company,String location)throws SQLException{
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("INSERT OR IGNORE INTO users(id,full_name,email,password_hash,role,title,company_name,location) VALUES(?,?,?,?,?,?,?,?)")){
            ps.setString(1,id);ps.setString(2,name);ps.setString(3,email);ps.setString(4,hash);ps.setString(5,role.name());ps.setString(6,title);ps.setString(7,company);ps.setString(8,location);ps.executeUpdate();
        }
    }
    public void updateProfile(String id,String name,String title,String location,String about)throws SQLException{
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("UPDATE users SET full_name=?,title=?,location=?,about=? WHERE id=?")){ps.setString(1,name);ps.setString(2,title);ps.setString(3,location);ps.setString(4,about);ps.setString(5,id);ps.executeUpdate();}
    }
    public void updateMember(String id,String name,String email,String title,Role role)throws SQLException{
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("UPDATE users SET full_name=?,email=?,title=?,role=? WHERE id=?")){ps.setString(1,name);ps.setString(2,email);ps.setString(3,title);ps.setString(4,role.name());ps.setString(5,id);ps.executeUpdate();}
    }
    public void setActive(String id,boolean active)throws SQLException{try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("UPDATE users SET active=? WHERE id=?")){ps.setInt(1,active?1:0);ps.setString(2,id);ps.executeUpdate();}}
    public void delete(String id)throws SQLException{try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("DELETE FROM users WHERE id=?")){ps.setString(1,id);ps.executeUpdate();}}
    private User map(ResultSet rs)throws SQLException{
        String id=rs.getString("id"),name=rs.getString("full_name"),email=rs.getString("email"),title=rs.getString("title"),location=rs.getString("location"),about=rs.getString("about");boolean active=rs.getInt("active")==1;
        return switch(Role.valueOf(rs.getString("role"))){
            case JOB_SEEKER->new JobSeeker(id,name,email,title,location,about,active);
            case EMPLOYER->new Employer(id,name,email,title,location,about,active,rs.getString("company_name"));
            case ADMIN->new Administrator(id,name,email,title,location,about,active);
        };
    }
}
