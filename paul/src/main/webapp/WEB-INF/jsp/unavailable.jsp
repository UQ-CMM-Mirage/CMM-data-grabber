<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber - Access Restricted</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>Data Grabber- Service Unavailable</h1>
		<div class="alert alert-success">
			This service is temporarily unavailable.
		</div>
		<button class="btn btn-small" type="button" onclick="window.location = 'index.jsp'">OK</button>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>