<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title><c:out value="${pageTitle}" default="TalentBridge"/> · TalentBridge</title>
  <link rel="stylesheet" href="<c:out value='${pageContext.request.contextPath}'/>/assets/portal.css">
</head>
<body>
<header class="topbar">
  <a class="brand" href="<c:out value='${pageContext.request.contextPath}'/>/dashboard"><span class="brand-mark">T</span><span>TalentBridge<small>Find work that moves you</small></span></a>
  <nav class="nav-links" aria-label="Main navigation">
    <a href="<c:out value='${pageContext.request.contextPath}'/>/dashboard">Dashboard</a>
    <a href="<c:out value='${pageContext.request.contextPath}'/>/jobs">Explore jobs</a>
    <a href="<c:out value='${pageContext.request.contextPath}'/>/profile">Profile</a>
  </nav>
  <div class="account"><span class="avatar"><c:out value="${currentUser.name.substring(0,1)}"/></span><span class="account-name"><c:out value="${currentUser.name}"/><small><c:out value="${currentUser.dashboardLabel}"/></small></span>
    <form method="post" action="<c:out value='${pageContext.request.contextPath}'/>/logout"><input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrfToken}'/>"><button class="link-button" type="submit">Sign out</button></form>
  </div>
</header>
<main class="page-shell">
  <c:if test="${not empty flashMessage}"><div class="alert <c:out value='${flashKind}'/>" role="status"><c:out value="${flashMessage}"/></div></c:if>
