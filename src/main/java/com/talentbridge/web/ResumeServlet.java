package com.talentbridge.web;

import com.talentbridge.db.ApplicationDao;
import com.talentbridge.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/resume")
public final class ResumeServlet extends WebSupport {
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = currentUser(request);
        try {
            var file = ((ApplicationDao) getServletContext().getAttribute(PortalContextListener.APPLICATIONS))
                    .findResumeForParticipant(value(request, "applicationId", 80), user.getId()).orElse(null);
            if (file == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            Path path = Path.of(file.path()).toAbsolutePath().normalize();
            if (!Files.isRegularFile(path)) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            String safeName = file.fileName().replaceAll("[\\r\\n\\\"]", "_");
            response.setContentType("application/octet-stream");
            response.setHeader("X-Content-Type-Options", "nosniff");
            response.setHeader("Content-Disposition", "attachment; filename=\"resume\"; filename*=UTF-8''" + URLEncoder.encode(safeName, StandardCharsets.UTF_8).replace("+", "%20"));
            response.setContentLengthLong(Files.size(path));
            try (var input = Files.newInputStream(path); var output = response.getOutputStream()) { input.transferTo(output); }
        } catch (SQLException ex) { throw new ServletException("Could not retrieve the resume", ex); }
    }
}
