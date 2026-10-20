package com.talentbridge.web;

import com.talentbridge.model.Role;
import com.talentbridge.model.User;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

abstract class WebSupport extends HttpServlet {
    protected User currentUser(HttpServletRequest request) { return (User) request.getAttribute("currentUser"); }
    protected void flash(HttpServletRequest request, String kind, String message) {
        request.getSession().setAttribute("flashKind", kind);
        request.getSession().setAttribute("flashMessage", message);
    }
    protected void takeFlash(HttpServletRequest request) {
        Object message = request.getSession().getAttribute("flashMessage");
        if (message != null) {
            request.setAttribute("flashMessage", message);
            request.setAttribute("flashKind", request.getSession().getAttribute("flashKind"));
            request.getSession().removeAttribute("flashMessage");
            request.getSession().removeAttribute("flashKind");
        }
    }
    protected void redirectDashboard(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
    protected boolean requireRole(HttpServletRequest request, HttpServletResponse response, Role... roles) throws IOException {
        User user = currentUser(request);
        for (Role role : roles) if (user != null && user.getRole() == role) return true;
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }
    protected String value(HttpServletRequest request, String name, int maxLength) {
        String raw = request.getParameter(name);
        if (raw == null) return "";
        String trimmed = raw.trim();
        return trimmed.length() > maxLength ? trimmed.substring(0, maxLength) : trimmed;
    }
}
