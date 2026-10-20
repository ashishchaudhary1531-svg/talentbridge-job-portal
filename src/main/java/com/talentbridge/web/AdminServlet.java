package com.talentbridge.web;

import com.talentbridge.db.JobDao;
import com.talentbridge.db.SystemSettingsDao;
import com.talentbridge.db.UserDao;
import com.talentbridge.model.Role;
import com.talentbridge.model.User;
import com.talentbridge.service.BackgroundTaskManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/manage")
public final class AdminServlet extends WebSupport {
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!requireRole(request, response, Role.ADMIN)) return;
        User actor = currentUser(request);
        String action = value(request, "action", 30), id = value(request, "id", 80);
        UserDao users = (UserDao) getServletContext().getAttribute(PortalContextListener.USERS);
        JobDao jobs = (JobDao) getServletContext().getAttribute(PortalContextListener.JOBS);
        try {
            switch (action) {
                case "job-status" -> {
                    String status = value(request, "status", 20);
                    if (!java.util.Set.of("APPROVED", "REJECTED").contains(status)) { response.sendError(400); return; }
                    boolean updated = jobs.updateStatus(id, status);
                    flash(request, updated ? "success" : "error", updated ? "Job review saved." : "Job posting was not found.");
                    if (updated) record(actor, status.toLowerCase() + " a job posting");
                }
                case "settings" -> {
                    SystemSettingsDao settings = (SystemSettingsDao) getServletContext().getAttribute(PortalContextListener.SETTINGS);
                    settings.save("portal.welcome", value(request, "welcome", 180));
                    settings.save("portal.supportEmail", value(request, "supportEmail", 254));
                    flash(request, "success", "Portal settings saved."); record(actor, "updated portal settings");
                }
                case "user-active" -> {
                    if (id.equals(actor.getId())) { flash(request, "error", "You cannot deactivate your own administrator account."); break; }
                    users.setActive(id, "true".equals(request.getParameter("active")));
                    flash(request, "success", "User access updated."); record(actor, "updated a user account");
                }
                case "user-update" -> {
                    if (id.equals(actor.getId())) { flash(request, "error", "You cannot change your own administrator role here."); break; }
                    Role role;
                    try { role = Role.valueOf(value(request, "role", 20)); }
                    catch (IllegalArgumentException ex) { response.sendError(400); return; }
                    users.updateMember(id, value(request, "name", 100), value(request, "email", 254), value(request, "title", 120), role);
                    flash(request, "success", "User details updated."); record(actor, "edited a user account");
                }
                case "user-delete" -> {
                    if (id.equals(actor.getId())) { flash(request, "error", "You cannot delete your own administrator account."); break; }
                    users.delete(id); flash(request, "success", "User account and associated records deleted."); record(actor, "deleted a user account");
                }
                default -> { response.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
            }
            redirectDashboard(request, response);
        } catch (SQLException ex) { throw new ServletException("Could not apply the administrator action", ex); }
    }
    private void record(User user, String description) {
        ((BackgroundTaskManager) getServletContext().getAttribute(PortalContextListener.TASKS)).recordActivity(user.getId(), user.getName() + " " + description);
    }
}
