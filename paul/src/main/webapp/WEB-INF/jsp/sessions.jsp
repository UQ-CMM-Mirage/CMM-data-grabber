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
		<table class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th>Facility name</th>
					<th>User</th>
					<th>Session started</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${facilities}" var="facility">
					<c:choose>
						<c:when test="${facility.dummy}"></c:when>
						<c:when test="${facility.inUse}">
							<tr>
								<td>${facility.facilityName}</td>
								<td>${facility.currentSession.userName}</td>
								<td>${facility.currentSession.loginTime}</td>
								<td>
									<form class="btn-group" action="sessions" method="post">
										<input type="hidden" name="sessionUuid"
											value="${facility.currentSession.sessionUuid}">
										<button class="btn" name="endSession" type="submit">End
											session</button>
									</form>
								</td>
							</tr>
						</c:when>
						<c:otherwise>
							<tr>
								<td>${facility.facilityName}</td>
								<td colspan="2">not in use</td>
								<td>
									<form class="btn-group" action="facilityLogin" method="post">
										<input type="hidden"
											name="facilityName" value="${facility.facilityName}">
										<input type="hidden" name="returnTo" value="/sessions">
										<button class="btn" name="startSession" type="submit">Start
											session</button>
									</form>
								</td>
							</tr>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>