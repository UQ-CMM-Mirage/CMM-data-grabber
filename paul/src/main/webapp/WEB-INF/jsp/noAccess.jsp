<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber - Access Restricted</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>Data Grabber- Access Restricted</h1>
		<p>
			Access to this information or functionality is limited
			to people with Administrator rights.
		</p>
		<button onclick="window.location = 'index.jsp'">OK</button>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>