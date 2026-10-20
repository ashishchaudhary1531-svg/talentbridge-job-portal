package com.talentbridge.web;

import com.talentbridge.db.*;
import com.talentbridge.model.Job;
import com.talentbridge.model.Role;
import com.talentbridge.model.User;
import com.talentbridge.service.BackgroundTaskManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;

@WebServlet("/applications")
@MultipartConfig(maxFileSize = 5_242_880, maxRequestSize = 6_291_456)
public final class ApplicationServlet extends WebSupport {
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = currentUser(request);
        String action = value(request, "action", 20);
        ApplicationDao applications = (ApplicationDao) getServletContext().getAttribute(PortalContextListener.APPLICATIONS);
        try {
            if (action.equals("apply")) {
                if (!requireRole(request, response, Role.JOB_SEEKER)) return;
                String jobId = value(request, "jobId", 80);
                Job job = ((JobDao) getServletContext().getAttribute(PortalContextListener.JOBS)).findById(jobId).orElse(null);
                if (job == null || !"APPROVED".equals(job.getStatus())) {
                    flash(request, "error", "This job is no longer accepting applications."); response.sendRedirect(request.getContextPath() + "/jobs"); return;
                }
                String resumeName = "", resumePath = "";
                Part resume = request.getPart("resume");
                if (resume != null && resume.getSize() > 0) {
                    String submittedName = Path.of(resume.getSubmittedFileName() == null ? "resume" : resume.getSubmittedFileName()).getFileName().toString();
                    String extension = submittedName.contains(".") ? submittedName.substring(submittedName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT) : "";
                    if (!java.util.Set.of("pdf", "doc", "docx").contains(extension)) {
                        flash(request, "error", "Upload a PDF, DOC, or DOCX resume."); response.sendRedirect(request.getContextPath() + "/jobs"); return;
                    }
                    Path root = Path.of(System.getProperty("talentbridge.uploads", "data/uploads")).toAbsolutePath();
                    Files.createDirectories(root);
                    String storedName = UUID.randomUUID() + "." + extension;
                    try (var input = resume.getInputStream()) { Files.copy(input, root.resolve(storedName)); }
                    resumeName = submittedName; resumePath = root.resolve(storedName).toString();
                }
                try {
                    applications.apply(jobId, user.getId(), value(request, "coverNote", 1500), resumeName, resumePath);
                    flash(request, "success", "Application sent to " + job.getCompanyName() + ". You can follow its progress from your dashboard."); record(user, "applied for " + job.getTitle());
                } catch (SQLException failure) {
                    if (failure.getMessage() != null && failure.getMessage().contains("UNIQUE constraint failed")) {
                        flash(request, "error", "You have already applied to this job.");
                    } else throw failure;
                }
                response.sendRedirect(request.getContextPath() + "/jobs");
            } else if (action.equals("status")) {
                if (!requireRole(request, response, Role.EMPLOYER)) return;
                String status = value(request, "status", 20);
                if (!java.util.Set.of("IN_REVIEW", "SHORTLISTED", "INTERVIEW", "OFFER", "HIRED", "REJECTED").contains(status)) { response.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
                boolean changed = applications.updateStatus(value(request, "applicationId", 80), user.getId(), status);
                flash(request, changed ? "success" : "error", changed ? "Application status updated." : "Application not found for your company."); redirectDashboard(request, response);
            } else { response.sendError(HttpServletResponse.SC_BAD_REQUEST); }
        } catch (SQLException ex) { throw new ServletException("Could not save this application", ex); }
    }
    private void record(User user, String description) {
        ((BackgroundTaskManager) getServletContext().getAttribute(PortalContextListener.TASKS)).recordActivity(user.getId(), user.getName() + " " + description);
    }
}
