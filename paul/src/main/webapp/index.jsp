<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag" %>
<title>Data Grabber</title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag" %>
	<div class="container">
	<h1>Data Grabber Main Menu</h1>
	<ul>
		<li><a href="/paul/admin">Data Grabber Administration</a> - CMM staff only.</li>
		<li><a href="/paul/aclsLogin">Start an Instrument Session</a> - when instrument login isn't working</li>
		<li><a href="/paul/claim">Claim files</a> - for files created when you weren't logged in</li>
		<li><a href="mirage">Go to the Mirage Repository</a> - to access your files</li>
	</ul>
	</div><!-- /container -->
<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag" %>
</body>
</html>