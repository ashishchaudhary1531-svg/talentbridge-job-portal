<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Sign in · TalentBridge</title><link rel="stylesheet" href="<c:out value='${pageContext.request.contextPath}'/>/assets/portal.css"></head>
<body class="login-page"><main class="login-layout">
  <section class="login-story"><a class="brand brand-light" href="#"><span class="brand-mark">T</span><span>TalentBridge<small>Find work that moves you</small></span></a><div><p class="eyebrow">YOUR NEXT CHAPTER STARTS HERE</p><h1>Good work begins with the right connection.</h1><p>Discover roles, meet great teams, and keep every application moving in one place.</p><div class="story-points"><span>01 &nbsp; Find opportunities that fit</span><span>02 &nbsp; Connect with hiring teams</span><span>03 &nbsp; Track your next move</span></div></div><span class="story-foot">A simple place to move your career forward.</span></section>
  <section class="login-panel"><div class="login-card"><p class="eyebrow">WELCOME BACK</p><h2>Sign in to TalentBridge</h2><p class="muted">Use one of the demo accounts to explore the portal.</p>
    <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>
    <c:if test="${not empty flashMessage}"><div class="alert <c:out value='${flashKind}'/>"><c:out value="${flashMessage}"/></div></c:if>
    <c:if test="${param.disabled eq '1'}"><div class="alert error">This account is unavailable. Contact the administrator.</div></c:if>
    <form method="post" action="<c:out value='${pageContext.request.contextPath}'/>/login" class="form-stack">
      <input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrfToken}'/>">
      <label>Email address<input type="email" name="email" autocomplete="username" required maxlength="254" placeholder="you@example.com" value="<c:out value='${param.email}'/>"></label>
      <label>Password<input type="password" name="password" autocomplete="current-password" required maxlength="100" placeholder="Your password"></label>
      <button class="button primary full" type="submit">Sign in <span aria-hidden="true">→</span></button>
    </form>
    <p class="signup-line">New to TalentBridge? <a href="<c:out value='${pageContext.request.contextPath}'/>/register">Create an account</a></p>
    <div class="demo-box"><strong>Demo access</strong><div><span>Administrator</span><code>admin@talentbridge.demo</code></div><div><span>Employer</span><code>employer@talentbridge.demo</code></div><div><span>Job seeker</span><code>seeker@talentbridge.demo</code></div><small>Password for all: <code>demo1234</code></small></div>
  </div><p class="login-legal">Demo data is stored locally in the application database.</p></section>
</main></body></html>
