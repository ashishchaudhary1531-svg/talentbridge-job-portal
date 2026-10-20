package com.talentbridge.web;

import com.talentbridge.db.UserDao;
import com.talentbridge.model.User;
import com.talentbridge.security.PasswordHasher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/login")
public final class LoginServlet extends WebSupport {
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        takeFlash(request);
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = value(request, "email", 254).toLowerCase();
        String password = request.getParameter("password");
        try {
            Optional<UserDao.Credentials> credentials = ((UserDao) getServletContext().getAttribute(PortalContextListener.USERS)).findCredentials(email);
            if (credentials.isEmpty() || !PasswordHasher.verify(password == null ? "" : password, credentials.get().passwordHash()) || !credentials.get().user().isActive()) {
                request.setAttribute("error", "Email or password is incorrect.");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                return;
            }
            request.changeSessionId();
            request.getSession().setAttribute("userId", credentials.get().user().getId());
            redirectDashboard(request, response);
        } catch (SQLException ex) { throw new ServletException("Could not sign you in", ex); }
    }
}
