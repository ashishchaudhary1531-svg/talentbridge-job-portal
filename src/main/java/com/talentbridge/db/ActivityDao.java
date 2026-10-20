package com.talentbridge.db;
import java.sql.*;
import java.util.*;
public final class ActivityDao {
    public record Activity(String description,String createdAt){}
    public void add(String actorId,String description)throws SQLException{
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("INSERT INTO activity_log(id,actor_id,description) VALUES(?,?,?)")){ps.setString(1,UUID.randomUUID().toString());ps.setString(2,actorId);ps.setString(3,description);ps.executeUpdate();}
    }
    public List<Activity> recent(int limit)throws SQLException{
        List<Activity> items=new ArrayList<>();try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("SELECT description,created_at FROM activity_log ORDER BY created_at DESC LIMIT ?")){ps.setInt(1,Math.max(1,Math.min(limit,100)));try(ResultSet rs=ps.executeQuery()){while(rs.next())items.add(new Activity(rs.getString(1),rs.getString(2)));}}return items;
    }
}
