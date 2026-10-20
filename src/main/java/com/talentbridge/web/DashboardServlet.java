package com.talentbridge.web;

import com.talentbridge.db.*;
import com.talentbridge.model.Role;
import com.talentbridge.model.User;
import com.talentbridge.model.Job;
import com.talentbridge.model.BarMetric;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/dashboard")
public final class DashboardServlet extends WebSupport {
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = currentUser(request);
        JobDao jobs = (JobDao) getServletContext().getAttribute(PortalContextListener.JOBS);
        ApplicationDao applications = (ApplicationDao) getServletContext().getAttribute(PortalContextListener.APPLICATIONS);
        try {
            request.setAttribute("settings", ((SystemSettingsDao) getServletContext().getAttribute(PortalContextListener.SETTINGS)).findAll());
            if (user.getRole() == Role.JOB_SEEKER) {
                request.setAttribute("applications", applications.findForSeeker(user.getId()));
                request.setAttribute("jobCount", jobs.countByStatus("APPROVED"));
                request.setAttribute("applicationCount", applications.countForSeeker(user.getId()));
                var alreadyApplied = applications.findForSeeker(user.getId()).stream().map(app -> app.getJobId()).collect(java.util.stream.Collectors.toSet());
                request.setAttribute("recommendedJobs", jobs.searchApproved(user.getTitle(), user.getLocation(), "").stream()
                        .filter(job -> !alreadyApplied.contains(job.getId())).limit(3).toList());
            } else if (user.getRole() == Role.EMPLOYER) {
                request.setAttribute("jobs", jobs.findForEmployer(user.getId()));
                request.setAttribute("applications", applications.findForEmployer(user.getId()));
                request.setAttribute("jobCount", jobs.countForEmployer(user.getId()));
                request.setAttribute("applicationCount", applications.countForEmployer(user.getId()));
                request.setAttribute("statusMetrics", metrics(applications.statusCountsForEmployer(user.getId())));
            } else {
                UserDao users = (UserDao) getServletContext().getAttribute(PortalContextListener.USERS);
                request.setAttribute("users", users.findAll());
                request.setAttribute("jobs", jobs.findPending());
                request.setAttribute("recentActivity", ((ActivityDao) getServletContext().getAttribute(PortalContextListener.ACTIVITY)).recent(10));
                request.setAttribute("userCount", users.count());
                request.setAttribute("jobCount", jobs.countAll());
                request.setAttribute("applicationCount", applications.countAll());
                request.setAttribute("pendingJobCount", jobs.countByStatus("PENDING"));
                request.setAttribute("statusMetrics", metrics(applications.statusCounts()));
                request.setAttribute("jobReviewMetrics", metrics(java.util.Map.of(
                        "Approved", jobs.countByStatus("APPROVED"), "Pending", jobs.countByStatus("PENDING"),
                        "Rejected", jobs.countByStatus("REJECTED"), "Closed", jobs.countByStatus("CLOSED"))));
            }
            takeFlash(request);
            request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
        } catch (SQLException ex) { throw new ServletException("Could not load your dashboard", ex); }
    }

    private static java.util.List<BarMetric> metrics(java.util.Map<String,Integer> counts) {
        int max = counts.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        return counts.entrySet().stream().map(entry -> new BarMetric(entry.getKey().replace('_', ' '), entry.getValue(),
                max == 0 ? 0 : Math.max(8, (int) Math.round(entry.getValue() * 100.0 / max)))).toList();
    }
}
