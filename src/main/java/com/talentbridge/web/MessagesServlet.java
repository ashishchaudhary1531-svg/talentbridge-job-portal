package com.talentbridge.web;

import com.talentbridge.db.ApplicationDao;
import com.talentbridge.db.MessageDao;
import com.talentbridge.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/messages")
public final class MessagesServlet extends WebSupport {
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String appId = value(request, "applicationId", 80);
        try {
            if (!participant(request, response, appId)) return;
            request.setAttribute("applicationId", appId);
            request.setAttribute("messages", ((MessageDao) getServletContext().getAttribute(PortalContextListener.MESSAGES)).findForApplication(appId));
            takeFlash(request);
            request.getRequestDispatcher("/WEB-INF/views/messages.jsp").forward(request, response);
        } catch (SQLException ex) { throw new ServletException("Could not load conversation", ex); }
    }
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = currentUser(request); String appId = value(request, "applicationId", 80), body = value(request, "body", 2000);
        try {
            if (!participant(request, response, appId)) return;
            if (body.isBlank()) { flash(request, "error", "Write a message before sending."); }
            else ((MessageDao) getServletContext().getAttribute(PortalContextListener.MESSAGES)).send(appId, user.getId(), body);
            response.sendRedirect(request.getContextPath() + "/messages?applicationId=" + java.net.URLEncoder.encode(appId, java.nio.charset.StandardCharsets.UTF_8));
        } catch (SQLException ex) { throw new ServletException("Could not send the message", ex); }
    }
    private boolean participant(HttpServletRequest request, HttpServletResponse response, String appId) throws SQLException, IOException {
        if (!((ApplicationDao) getServletContext().getAttribute(PortalContextListener.APPLICATIONS)).isParticipant(appId, currentUser(request).getId())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN); return false;
        }
        return true;
    }
}
