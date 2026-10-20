<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Explore jobs"/><%@ include file="_top.jsp" %>
<section class="page-heading"><div><p class="eyebrow">OPPORTUNITY, WITH DIRECTION</p><h1>Explore jobs</h1><p>Search approved roles from teams looking for their next great colleague.</p></div><div class="heading-note"><span class="note-dot"></span>Updated as new opportunities are approved</div></section>
<form class="search-panel" method="get" action="<c:out value='${pageContext.request.contextPath}'/>/jobs">
  <label class="search-field"><span>Keywords</span><input name="q" placeholder="Role, skill, or company" value="<c:out value='${query}'/>" maxlength="100"></label>
  <label class="search-field"><span>Location</span><input name="location" placeholder="City or remote" value="<c:out value='${locationFilter}'/>" maxlength="100"></label>
  <label class="search-field"><span>Category</span><select name="category"><option value="">All categories</option><c:forEach items="${['Design','Engineering','Product','Marketing','Operations','Data']}" var="cat"><option value="<c:out value='${cat}'/>" <c:if test="${categoryFilter eq cat}">selected</c:if>><c:out value="${cat}"/></option></c:forEach></select></label>
  <button class="button primary" type="submit">Search roles</button>
</form>
<c:choose><c:when test="${empty jobs}"><div class="empty-state"><span class="empty-icon">⌕</span><h2>No roles match those filters</h2><p>Try another keyword or broaden the location.</p><a class="button secondary" href="<c:out value='${pageContext.request.contextPath}'/>/jobs">Clear filters</a></div></c:when><c:otherwise>
  <div class="results-head"><strong><c:out value="${jobs.size()}"/> opportunities</strong><span>Showing approved job listings</span></div>
  <div class="job-grid"><c:forEach items="${jobs}" var="job"><article class="job-card">
    <div class="job-card-top"><span class="company-mark"><c:out value="${job.companyName.substring(0,1)}"/></span><span class="pill green">OPEN</span></div>
    <p class="job-company"><c:out value="${job.companyName}"/></p><h2><c:out value="${job.title}"/></h2>
    <div class="job-meta"><span>⌖ <c:out value="${job.location}"/></span><span>◷ <c:out value="${job.employmentType.replace('_',' ')}"/></span><span>⌂ <c:out value="${job.workMode}"/></span></div>
    <p class="job-description"><c:out value="${job.description}"/></p><div class="job-bottom"><span class="salary"><c:out value="${job.salary}"/></span><span><c:out value="${job.category}"/></span></div>
    <c:if test="${currentUser.roleName eq 'JOB_SEEKER'}"><details class="apply-details"><summary class="button primary">Apply now <span aria-hidden="true">→</span></summary><form method="post" enctype="multipart/form-data" action="<c:out value='${pageContext.request.contextPath}'/>/applications" class="form-stack apply-form">
      <input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrfToken}'/>"><input type="hidden" name="action" value="apply"><input type="hidden" name="jobId" value="<c:out value='${job.id}'/>">
      <label>Short note to the hiring team<textarea name="coverNote" maxlength="1500" rows="3" placeholder="Why are you interested?"></textarea></label><label>Resume <small>(PDF, DOC or DOCX; max 5 MB)</small><input type="file" name="resume" accept=".pdf,.doc,.docx"></label><button class="button dark full" type="submit">Send application</button>
    </form></details></c:if>
  </article></c:forEach></div>
</c:otherwise></c:choose>
<%@ include file="_bottom.jsp" %>
