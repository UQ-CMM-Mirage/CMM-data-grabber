<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber - Current Facility Sessions</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>Current Facility Sessions</h1>
		<table class="table table-striped">
			<thead>
				<tr>
					<th class="span3">Facility name</th>
					<th class="span2">User</th>
					<th class="span3">Session started</th>
					<th class="span4"></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${sessions}" var="session">
					<c:choose>
						<c:when test="${empty session.logoutTime && !empty session.loginTime}">
							<tr>
								<td>${session.facilityName}</td>
								<td>${session.userName}</td>
								<td><fmt:formatDate value="${session.loginTime}" 
										type="both" dateStyle="medium"/>
								</td>
								<td class="form-inline">
								    <form class="btn" action="sessions" method="post">
										<input type="hidden" name="sessionUuid"
											value="${session.sessionUuid}">
										<button class="btn" name="endSession" type="submit">End
											Session</button>
									</form>
									<form class="btn" action="facilities/${session.facilityName}">
										<input type="hidden" name="returnTo" value="/sessions">
										<button class="btn" type="submit" name="sessionLog">View Session 
											Log</button>
									</form>
								</td>
							</tr>
						</c:when>
						<c:otherwise>
							<tr>
								<td>${session.facilityName}</td>
								<td colspan="2">not in use</td>
								<td class="form-inline">
								    <form class="btn" action="facilityLogin" method="post">
										<input type="hidden" name="facilityName"
											value="${session.facilityName}"> 
										<input type="hidden" name="returnTo" value="/sessions">
										<button class="btn" name="startSession" type="submit">Start
											Session</button>
									</form>
									<form class="btn" action="facilities/${session.facilityName}">
										<input type="hidden" name="returnTo" value="/sessions">
										<button class="btn" type="submit" name="sessionLog">View Session 
											Log</button>
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