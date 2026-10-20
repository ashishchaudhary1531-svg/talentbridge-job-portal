package com.talentbridge.service;

import com.talentbridge.db.*;
import com.talentbridge.model.Role;
import com.talentbridge.security.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

/** Adds predictable demo users and listings to a brand-new database only. */
public final class DemoDataSeeder {
    public static final String ADMIN_EMAIL = "admin@talentbridge.demo";
    public static final String EMPLOYER_EMAIL = "employer@talentbridge.demo";
    public static final String SEEKER_EMAIL = "seeker@talentbridge.demo";
    public static final String PASSWORD = "demo1234";

    private final UserDao users;
    public DemoDataSeeder(UserDao users) { this.users = users; }

    public void seedIfEmpty() throws SQLException {
        if (users.count() > 0) return;
        String admin = "demo-admin", employer = "demo-employer", seeker = "demo-seeker";
        users.insert(admin, "Alex Morgan", ADMIN_EMAIL, PasswordHasher.hash(PASSWORD), Role.ADMIN, "Portal administrator", "", "Remote");
        users.insert(employer, "Jordan Lee", EMPLOYER_EMAIL, PasswordHasher.hash(PASSWORD), Role.EMPLOYER, "Talent Partner", "Northstar Studio", "Bengaluru, India");
        users.insert(seeker, "Sam Taylor", SEEKER_EMAIL, PasswordHasher.hash(PASSWORD), Role.JOB_SEEKER, "Product Designer", "", "Chennai, India");
        try (Connection c = Database.getConnection()) {
            insertJob(c, "demo-job-1", employer, "Product Designer", "Shape clear, accessible experiences for a growing digital product. Partner with engineering and research from discovery to delivery.", "Design", "Bengaluru, India", "FULL_TIME", "HYBRID", "₹12–18 LPA", "Figma, UX, research", "APPROVED");
            insertJob(c, "demo-job-2", employer, "Java Backend Developer", "Build reliable APIs and data services with a collaborative engineering team. Java and SQL experience are welcome.", "Engineering", "Remote, India", "FULL_TIME", "REMOTE", "₹10–16 LPA", "Java, JDBC, SQL", "APPROVED");
            insertJob(c, "demo-job-pending", employer, "Associate UX Researcher", "Help plan interviews, synthesize findings, and turn user needs into product opportunities.", "Design", "Chennai, India", "INTERNSHIP", "HYBRID", "₹25k/month", "research, usability", "PENDING");
        }
        String sampleApplication = new ApplicationDao().apply("demo-job-1", seeker,
                "I enjoy making complex tools feel simple and would love to bring my research-led design approach to Northstar Studio.", "", "");
        new MessageDao().send(sampleApplication, employer, "Thanks for applying, Sam. We are reviewing applications this week and will follow up with next steps.");
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("INSERT OR IGNORE INTO system_settings(setting_key,setting_value) VALUES(?,?)")) {
            ps.setString(1, "portal.welcome"); ps.setString(2, "Welcome to TalentBridge"); ps.executeUpdate();
            ps.setString(1, "portal.supportEmail"); ps.setString(2, "support@talentbridge.demo"); ps.executeUpdate();
        }
    }

    private static void insertJob(Connection c, String id, String employer, String title, String description,
                                  String category, String location, String type, String mode, String salary,
                                  String tags, String status) throws SQLException {
        String sql = "INSERT INTO jobs(id,employer_id,title,description,category,location,employment_type,work_mode,salary,tags,status,expires_at) VALUES(?,?,?,?,?,?,?,?,?,?,?,date('now','+60 day'))";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id); ps.setString(2, employer); ps.setString(3, title); ps.setString(4, description);
            ps.setString(5, category); ps.setString(6, location); ps.setString(7, type); ps.setString(8, mode);
            ps.setString(9, salary); ps.setString(10, tags); ps.setString(11, status); ps.executeUpdate();
        }
    }
}
