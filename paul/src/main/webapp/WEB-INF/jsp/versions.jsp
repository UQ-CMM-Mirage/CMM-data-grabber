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
		<table>
			<thead>
				<tr>
					<td>Group id</td>
					<td>Artifact id</td>
					<td>Version no</td>
					<td>Build timestamp</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td><c:out>${paul.groupId}</c:out></td>
					<td><c:out>${paul.artifactId}</c:out></td>
					<td><c:out>${paul.versionId}</c:out></td>
					<td><c:out>${paul.buildTimestamp}</c:out></td>
				</tr>
				<tr>
					<td><c:out>${eccles.groupId}</c:out></td>
					<td><c:out>${eccles.artifactId}</c:out></td>
					<td><c:out>${eccles.versionId}</c:out></td>
					<td><c:out>${eccles.buildTimestamp}</c:out></td>
				</tr>
				<tr>
					<td><c:out>${acslib.groupId}</c:out></td>
					<td><c:out>${acslib.artifactId}</c:out></td>
					<td><c:out>${acslib.versionId}</c:out></td>
					<td><c:out>${acslib.buildTimestamp}</c:out></td>
				</tr>
			</tbody>
		</table>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>