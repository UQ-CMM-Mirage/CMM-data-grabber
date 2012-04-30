<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber User</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>Data Grabber User ${user.userName}</h1>
		<ul>
			<li>User name: ${user.userName}</li>
			<li>Email address: ${user.emailAddress}</li>
			<li>Real name: ${user.humanReadableName}</li>
			<li>Accounts:
				<c:forEach var="account" items="${user.accounts}">
					${account} &nbsp;
				</c:forEach>
			</li>
			<li>Organization: ${user.orgName}</li>
			<li>Certificates:
				<c:choose>
					<c:when test="${empty user.certifications}">
						No certification recorded
					</c:when>
					<c:otherwise>
					    <ul>
							<c:forEach var="entry" items="${user.certifications}">
								<li>${entry.key} : ${entry.value}</li>
							</c:forEach>
						</ul>
					</c:otherwise>
				</c:choose> 
			</li>
		</ul>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>