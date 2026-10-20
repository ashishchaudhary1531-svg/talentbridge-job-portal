package com.talentbridge.web;

import com.talentbridge.db.UserDao;
import com.talentbridge.model.Role;
import com.talentbridge.security.PasswordHasher;
import com.talentbridge.service.BackgroundTaskManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet("/register")
public final class RegisterServlet extends WebSupport {
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = value(request, "name", 100), email = value(request, "email", 254).toLowerCase();
        String password = request.getParameter("password"), confirm = request.getParameter("confirmPassword");
        String title = value(request, "title", 120), location = value(request, "location", 100), company = value(request, "company", 150);
        String requestedRole = value(request, "role", 30);
        Role role = "EMPLOYER".equals(requestedRole) ? Role.EMPLOYER : Role.JOB_SEEKER;
        if (name.isBlank() || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$") || password == null || password.length() < 8 || !password.equals(confirm) || (role == Role.EMPLOYER && company.isBlank())) {
            request.setAttribute("error", "Check your name, email, password, and account details. Passwords must be at least 8 characters and match.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response); return;
        }
        try {
            UserDao users = (UserDao) getServletContext().getAttribute(PortalContextListener.USERS);
            users.insert(UUID.randomUUID().toString(), name, email, PasswordHasher.hash(password), role, title, company, location);
            ((BackgroundTaskManager) getServletContext().getAttribute(PortalContextListener.TASKS)).recordActivity(null, name + " created a " + role.name().toLowerCase().replace('_', ' ') + " account");
            flash(request, "success", "Your account is ready. Sign in to continue.");
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (SQLException ex) {
            request.setAttribute("error", ex.getMessage() != null && ex.getMessage().contains("users.email") ? "That email address is already registered." : "We could not create the account. Please check your details and try again.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }
}
