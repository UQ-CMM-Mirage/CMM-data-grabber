<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber - ACLS Facility Sessions</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container">
		<h1>ACLS Facility Sessions</h1>
		<c:forEach items="${facilities}" var="facility">
			<c:if test="${!facility.dummy}">
				<c:if test="${facility.inUse}">
					<form action="sessions/${facility.currentSession.sessionUuid}"
						method="post">
						${facility.facilityName} is in use - user :
						${facility.currentSession.userName}, started :
						${facility.currentSession.loginTime}
						<button name="endSession" type="submit">End session</button>
					</form>
				</c:if>
				<c:if test="${! facility.inUse}">
					<form action="facilities/${facility.facilityName}" method="post">
						${facility.facilityName} is idle
						<button name="startSession" type="submit">Start session</button>
					</form>
				</c:if>
			</c:if>
		</c:forEach>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>