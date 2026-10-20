<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Your profile"/><%@ include file="_top.jsp" %>
<section class="page-heading"><div><p class="eyebrow">YOUR PROFESSIONAL STORY</p><h1>Profile</h1><p>Keep your details current so the right opportunities can find you.</p></div></section>
<div class="two-column"><section class="panel"><div class="panel-heading"><div><h2>Profile details</h2><p>These details are visible in your portal account.</p></div></div>
  <form method="post" action="<c:out value='${pageContext.request.contextPath}'/>/profile" class="form-stack"><input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrfToken}'/>">
    <label>Full name<input required maxlength="100" name="name" value="<c:out value='${currentUser.name}'/>"></label>
    <label>Email address<input disabled value="<c:out value='${currentUser.email}'/>"><small>Email changes are managed by an administrator.</small></label>
    <label>Professional title<input maxlength="120" name="title" value="<c:out value='${currentUser.title}'/>" placeholder="e.g. Product Designer"></label>
    <label>Location<input maxlength="100" name="location" value="<c:out value='${currentUser.location}'/>" placeholder="City, country or remote"></label>
    <label>About you<textarea name="about" rows="6" maxlength="1500" placeholder="A short introduction..."><c:out value="${currentUser.about}"/></textarea></label>
    <div><button class="button primary" type="submit">Save profile</button></div>
  </form>
</section><aside class="panel profile-summary"><span class="avatar avatar-large"><c:out value="${currentUser.name.substring(0,1)}"/></span><h2><c:out value="${currentUser.name}"/></h2><p><c:out value="${currentUser.title}" default="Add a professional title"/></p><p class="muted"><c:out value="${currentUser.location}" default="Add a location"/></p><span class="pill blue"><c:out value="${currentUser.dashboardLabel}"/></span></aside></div>
<%@ include file="_bottom.jsp" %>
