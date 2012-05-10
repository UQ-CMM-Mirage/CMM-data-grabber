<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Confirm Facility Deletion</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>Confirm Facility Deletion for ${facilityName}</h1>
		<form class="form-inline btn-toolbar" name="form" method="POST" action="${facilityName}">
			This will permanently delete the configuration details for
			this facility.
			<br>
			<input type="hidden" name="confirmed">
			<input type="hidden" name="delete">
			<button class="btn-large" type="submit" name="delete">Yes - do it now</button>
			<button class="btn-large" type="button" onclick="window.location = '${returnTo}'">
				No - get me out of here</button>
		</form>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>