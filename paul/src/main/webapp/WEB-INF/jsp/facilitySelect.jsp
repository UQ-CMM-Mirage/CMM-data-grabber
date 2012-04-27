<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber Facility Select</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>
			<c:choose>
				<c:when test="${next == 'claimDatasets'}">
					Select the Instrument you were using
				</c:when>
				<c:when test="${next == 'facilityLogin'}">
					Select the Facility you want use
				</c:when>
				<c:otherwise>
					Facility Selection
				</c:otherwise>
			</c:choose>
		</h1>
		${message}
		<br> 
		<form action="facilitySelect" method="post">
			<select name="facilityName">
				<c:forEach items="${facilities}" var="facility">
				    <c:if test="${!facility.dummy}">
						<option value="${facility.facilityName}">
							${facility.facilityName}
						</option>
					</c:if>
				</c:forEach>
			</select>
		<br>
		    <input type="hidden" name="next" value="${next}">
			<button type="submit">OK</button>
			<button type="button" onclick="window.location = '/paul'">Cancel</button>
		</form>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>