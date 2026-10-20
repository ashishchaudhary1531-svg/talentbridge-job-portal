package com.talentbridge.web;

import com.talentbridge.db.UserDao;
import com.talentbridge.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public final class AuthenticationFilter implements Filter {
    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getServletPath();
        if (path.equals("/login") || path.equals("/register") || path.startsWith("/assets/") || path.equals("/health")) { chain.doFilter(request, response); return; }
        Object id = req.getSession(false) == null ? null : req.getSession(false).getAttribute("userId");
        if (!(id instanceof String userId)) { res.sendRedirect(req.getContextPath() + "/login"); return; }
        try {
            User user = ((UserDao) req.getServletContext().getAttribute(PortalContextListener.USERS)).findById(userId).orElse(null);
            if (user == null || !user.isActive()) {
                req.getSession().invalidate();
                res.sendRedirect(req.getContextPath() + "/login?disabled=1");
                return;
            }
            req.setAttribute("currentUser", user);
            chain.doFilter(request, response);
        } catch (SQLException ex) { throw new ServletException("Could not verify your account", ex); }
    }
}
