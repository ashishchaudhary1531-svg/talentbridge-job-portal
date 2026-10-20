package com.talentbridge.web;

import com.talentbridge.db.JobDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/jobs")
public final class JobSearchServlet extends WebSupport {
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("jobs", ((JobDao) getServletContext().getAttribute(PortalContextListener.JOBS))
                    .searchApproved(value(request, "q", 100), value(request, "location", 100), value(request, "category", 60)));
            request.setAttribute("query", value(request, "q", 100));
            request.setAttribute("locationFilter", value(request, "location", 100));
            request.setAttribute("categoryFilter", value(request, "category", 60));
            takeFlash(request);
            request.getRequestDispatcher("/WEB-INF/views/jobs.jsp").forward(request, response);
        } catch (SQLException ex) { throw new ServletException("Could not search job listings", ex); }
    }
}
