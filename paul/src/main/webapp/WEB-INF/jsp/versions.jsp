<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber Version Info</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>Data Grabber Version Info</h1>
		<table class="table table-striped table-condensed">
			<thead>
				<tr>
					<td>Group id</td>
					<td>Artifact id</td>
					<td>Version no</td>
					<td>Build timestamp</td>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${buildInfo}" var="bi">
					<tr>
						<td><c:out value="${bi.groupId}" /></td>
						<td><c:out value="${bi.artifactId}" /></td>
						<td><c:out value="${bi.version}" /></td>
						<td><c:out value="${bi.buildTimestamp}" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>