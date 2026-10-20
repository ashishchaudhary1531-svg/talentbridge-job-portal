package com.talentbridge.db;
import com.talentbridge.model.Message;
import java.sql.*;
import java.util.*;
public final class MessageDao {
    public List<Message> findForApplication(String appId)throws SQLException{
        List<Message> items=new ArrayList<>();String sql="SELECT m.id,m.sender_id,u.full_name,m.body,m.sent_at FROM messages m JOIN users u ON u.id=m.sender_id WHERE m.application_id=? ORDER BY m.sent_at";
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement(sql)){ps.setString(1,appId);try(ResultSet rs=ps.executeQuery()){while(rs.next())items.add(new Message(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5)));}}return items;
    }
    public void send(String appId,String senderId,String body)throws SQLException{
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("INSERT INTO messages(id,application_id,sender_id,body) VALUES(?,?,?,?)")){ps.setString(1,UUID.randomUUID().toString());ps.setString(2,appId);ps.setString(3,senderId);ps.setString(4,body);ps.executeUpdate();}
    }
}
