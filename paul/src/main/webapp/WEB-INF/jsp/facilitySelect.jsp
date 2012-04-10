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
	<div class="container">
		<h1>Facility Selection</h1>
		${message}
		<br> 
		<form action="facilitySelect" method="post">
			<select name="facilityName">
				<c:forEach items="${facilities}" var="facility">
					<option value="${facility.facilityName}">
						${facility.facilityName}
					</option>
				</c:forEach>
			</select>
		<br>
			<button type="submit">OK</button>
			<button type="button" onclick="window.location = '/paul'">Cancel</button>

		</form>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>