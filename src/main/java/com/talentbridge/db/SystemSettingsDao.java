package com.talentbridge.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/** JDBC access for small portal-wide configuration values. */
public final class SystemSettingsDao {
    public Map<String, String> findAll() throws SQLException {
        Map<String, String> settings = new LinkedHashMap<>();
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT setting_key,setting_value FROM system_settings ORDER BY setting_key"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) settings.put(rs.getString(1), rs.getString(2));
        }
        return settings;
    }

    public void save(String key, String value) throws SQLException {
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("INSERT INTO system_settings(setting_key,setting_value) VALUES(?,?) ON CONFLICT(setting_key) DO UPDATE SET setting_value=excluded.setting_value")) {
            ps.setString(1, key); ps.setString(2, value); ps.executeUpdate();
        }
    }
}
