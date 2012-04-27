<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber Admin Interface</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>Data Grabber Admin Menu</h1>
		<ul>
			<li><a href="control">Control Panel</a></li>
			<li><a href="config">Configuration</a></li>
			<li><a href="sessions">Facility Sessions</a></li>
			<li><a href="atom/queue">Ingestion Queue Atom Feed</a> (raw / browser rendered)</li>
			<li><a href="queue/ingestible">Ingestion Queue Admin</a></li>
			<li><a href="queue/held">Hold Queue Admin</a></li>
			<li><a href="users">Known Users Cache</a></li>
		</ul>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>