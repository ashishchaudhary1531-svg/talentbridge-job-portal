package com.talentbridge.web;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class CsrfFilter implements Filter {
    private static final SecureRandom RANDOM = new SecureRandom();
    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        if ("POST".equalsIgnoreCase(req.getMethod())) {
            Object expected = req.getSession().getAttribute("csrfToken");
            String submitted = req.getParameter("csrfToken");
            if (!(expected instanceof String token) || submitted == null ||
                    !MessageDigest.isEqual(token.getBytes(java.nio.charset.StandardCharsets.UTF_8), submitted.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Your form expired. Refresh the page and try again.");
                return;
            }
        } else {
            var session = req.getSession(true);
            if (session.getAttribute("csrfToken") == null) session.setAttribute("csrfToken", token());
        }
        chain.doFilter(request, response);
    }
    private static String token() { byte[] bytes = new byte[32]; RANDOM.nextBytes(bytes); return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes); }
}
