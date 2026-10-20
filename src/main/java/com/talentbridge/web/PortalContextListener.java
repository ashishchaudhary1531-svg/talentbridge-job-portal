package com.talentbridge.web;

import com.talentbridge.db.*;
import com.talentbridge.service.BackgroundTaskManager;
import com.talentbridge.service.DemoDataSeeder;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PortalContextListener implements ServletContextListener {
    private static final Logger LOG = Logger.getLogger(PortalContextListener.class.getName());
    public static final String USERS = "users", JOBS = "jobs", APPLICATIONS = "applications", MESSAGES = "messages", ACTIVITY = "activity", SETTINGS = "settings", TASKS = "tasks";

    @Override public void contextInitialized(ServletContextEvent event) {
        try {
            Database.initialize();
            UserDao users = new UserDao(); JobDao jobs = new JobDao(); ApplicationDao applications = new ApplicationDao();
            ActivityDao activity = new ActivityDao(); MessageDao messages = new MessageDao(); SystemSettingsDao settings = new SystemSettingsDao();
            new DemoDataSeeder(users).seedIfEmpty();
            event.getServletContext().setAttribute(USERS, users);
            event.getServletContext().setAttribute(JOBS, jobs);
            event.getServletContext().setAttribute(APPLICATIONS, applications);
            event.getServletContext().setAttribute(MESSAGES, messages);
            event.getServletContext().setAttribute(ACTIVITY, activity);
            event.getServletContext().setAttribute(SETTINGS, settings);
            event.getServletContext().setAttribute(TASKS, new BackgroundTaskManager(activity, jobs));
            LOG.info("TalentBridge portal initialized");
        } catch (SQLException | IOException ex) {
            LOG.log(Level.SEVERE, "TalentBridge could not initialize its database", ex);
            throw new IllegalStateException("Portal startup failed. Check the database path and permissions.", ex);
        }
    }

    @Override public void contextDestroyed(ServletContextEvent event) {
        Object task = event.getServletContext().getAttribute(TASKS);
        if (task instanceof BackgroundTaskManager manager) manager.close();
    }
}
