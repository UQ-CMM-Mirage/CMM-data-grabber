<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Claim Held Datasets</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container">
		<h1>Held Datasets for Instrument '${facilityName}</h1>
		<c:choose>
			<c:when test="${!empty queue}">
				<ul>
					<c:forEach var="dataset" items="${datasets}">
						<li>Queue entry # ${entry.id} - captured:
							${entry.captureTimestamp}</li>
					</c:forEach>
				</ul>
			</c:when>
			<c:otherwise>
				<p>There are no unclaimed datasets</p>
			</c:otherwise>
		</c:choose>
		<button onclick="window.location = '/paul'">CANCEL</button>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>