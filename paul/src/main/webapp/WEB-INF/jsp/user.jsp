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
	<div class="container">
		<h1>Data Grabber User ${user.userName}</h1>
		<ul>
			<li>User name: ${user.userName}</li>
			<li>Email address: ${user.emailAddress}</li>
			<li>Real name: ${user.humanReadableName}</li>
		</ul>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>