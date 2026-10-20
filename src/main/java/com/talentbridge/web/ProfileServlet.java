package com.talentbridge.web;

import com.talentbridge.db.UserDao;
import com.talentbridge.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/profile")
public final class ProfileServlet extends WebSupport {
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        takeFlash(request);
        request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
    }
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = currentUser(request);
        String name = value(request, "name", 100), title = value(request, "title", 120), location = value(request, "location", 100), about = value(request, "about", 1500);
        if (name.isBlank()) { flash(request, "error", "Please enter your name."); redirect(request, response); return; }
        try {
            ((UserDao) getServletContext().getAttribute(PortalContextListener.USERS)).updateProfile(user.getId(), name, title, location, about);
            flash(request, "success", "Your profile was updated."); redirect(request, response);
        } catch (SQLException ex) { throw new ServletException("Could not update your profile", ex); }
    }
    private void redirect(HttpServletRequest request, HttpServletResponse response) throws IOException { response.sendRedirect(request.getContextPath() + "/profile"); }
}
