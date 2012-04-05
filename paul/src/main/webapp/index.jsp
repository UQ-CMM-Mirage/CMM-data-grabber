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
		<li><a href="/paul/facilityLogin">Start an Instrument Session</a> - when login isn't working on the instrument</li>
		<li><a href="/paul/claim">Claim my files</a> - for files that you saved when you weren't logged in</li>
		<li><a href="/paul/mirage">Go to the Mirage Repository</a> - to access your files</li>
		<li><a href="/paul/acls">Go to the ACLS booking system</a> - to book an instrument session</li>
	</ul>
	</div><!-- /container -->
<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag" %>
</body>
</html>