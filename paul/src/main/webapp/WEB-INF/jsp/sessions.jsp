<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber - Current Facility Sessions</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container">
		<h1>Current Facility Sessions</h1>
		<c:forEach items="${facilities}" var="facility">
		    <c:choose>
				<c:when test="${facility.dummy}"></c:when>
				<c:when test="${facility.inUse}">
					<form action="sessions" method="post">
						${facility.facilityName} is in use - user :
						${facility.currentSession.userName}, started :
						${facility.currentSession.loginTime}
						<input type="hidden" name="sessionUuid"
							value="${facility.currentSession.sessionUuid}">
						<button name="endSession" type="submit">End session</button>
					</form>
				</c:when>
				<c:otherwise>
					<form action="facilityLogin" method="post">
						${facility.facilityName} is idle
						<input type="hidden" name="facilityName" 
							value="${facility.facilityName}">
						<input type="hidden" name="returnTo" value="/sessions">
						<button name="startSession" type="submit">Start session</button>
					</form>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>