package com.talentbridge.web;

import com.talentbridge.db.JobDao;
import com.talentbridge.model.Role;
import com.talentbridge.model.User;
import com.talentbridge.service.BackgroundTaskManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/employer/jobs")
public final class JobManagementServlet extends WebSupport {
    private static final String[] TYPES = {"FULL_TIME", "PART_TIME", "CONTRACT", "INTERNSHIP"};
    private static final String[] MODES = {"ONSITE", "HYBRID", "REMOTE"};
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!requireRole(request, response, Role.EMPLOYER)) return;
        User user = currentUser(request);
        JobDao jobs = (JobDao) getServletContext().getAttribute(PortalContextListener.JOBS);
        String action = value(request, "action", 20);
        try {
            if (action.equals("create") || action.equals("update")) {
                String title = value(request, "title", 100), description = value(request, "description", 4000);
                String category = value(request, "category", 60), location = value(request, "location", 100);
                String type = choice(value(request, "employmentType", 30), TYPES, "FULL_TIME");
                String mode = choice(value(request, "workMode", 30), MODES, "HYBRID");
                if (title.isBlank() || description.length() < 25 || category.isBlank() || location.isBlank()) {
                    flash(request, "error", "Add a title, category, location, and a description of at least 25 characters.");
                } else if (action.equals("create")) {
                    jobs.create(user.getId(), title, description, category, location, type, mode, value(request, "salary", 80), value(request, "tags", 180));
                    flash(request, "success", "Your job was submitted for administrator review."); record(user, "submitted a job posting");
                } else {
                    boolean updated = jobs.update(value(request, "jobId", 80), user.getId(), title, description, category, location, type, mode, value(request, "salary", 80), value(request, "tags", 180));
                    flash(request, updated ? "success" : "error", updated ? "Job updated and sent for review." : "That job could not be updated.");
                    if (updated) record(user, "updated a job posting");
                }
            } else if (action.equals("close") || action.equals("reopen")) {
                boolean changed = jobs.updateStatusForEmployer(value(request, "jobId", 80), user.getId(), action.equals("close") ? "CLOSED" : "PENDING");
                flash(request, changed ? "success" : "error", changed ? (action.equals("close") ? "Job posting closed." : "Job submitted for review.") : "That job could not be changed.");
            } else if (action.equals("delete")) {
                boolean deleted = jobs.deleteForEmployer(value(request, "jobId", 80), user.getId());
                flash(request, deleted ? "success" : "error", deleted ? "Job posting deleted." : "That job could not be deleted.");
            } else { response.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
            redirectDashboard(request, response);
        } catch (SQLException ex) { throw new ServletException("Could not save the job posting", ex); }
    }
    private static String choice(String value, String[] choices, String fallback) {
        for (String allowed : choices) if (allowed.equals(value)) return allowed;
        return fallback;
    }
    private void record(User user, String description) {
        ((BackgroundTaskManager) getServletContext().getAttribute(PortalContextListener.TASKS)).recordActivity(user.getId(), user.getName() + " " + description);
    }
}
