package com.talentbridge.db;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
public final class Database {
    private static final String JDBC_URL=createJdbcUrl();
    private static boolean initialized;
    private Database(){}
    private static String createJdbcUrl(){
        try{Path file=Path.of(System.getProperty("talentbridge.db","data/talentbridge.db")).toAbsolutePath();Files.createDirectories(file.getParent());return "jdbc:sqlite:"+file;}
        catch(IOException ex){throw new ExceptionInInitializerError("Cannot create database directory: "+ex.getMessage());}
    }
    public static Connection getConnection() throws SQLException{
        Connection c=DriverManager.getConnection(JDBC_URL);
        try(Statement s=c.createStatement()){s.execute("PRAGMA foreign_keys=ON");s.execute("PRAGMA busy_timeout=5000");}
        return c;
    }
    public static synchronized void initialize() throws SQLException,IOException{
        if(initialized)return;
        try(InputStream in=Database.class.getResourceAsStream("/schema.sql")){
            if(in==null)throw new IOException("schema.sql was not packaged");
            String schema=new String(in.readAllBytes(),StandardCharsets.UTF_8);
            try(Connection c=getConnection();Statement s=c.createStatement()){
                s.execute("PRAGMA journal_mode=WAL");
                for(String sql:schema.split(";")){String command=sql.strip();if(!command.isEmpty())s.execute(command);}
            }
        }
        initialized=true;
    }
}
