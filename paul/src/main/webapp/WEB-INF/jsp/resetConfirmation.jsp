<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber Configuration Reset</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>Data Grabber Configuration Reset</h1>
		<form name="form" method="POST" action="${returnTo}">
			Resetting the configuration will permanently delete any manual
			configuration changes. <br> <input type="hidden" name="reset">
			<input type="hidden" name="confirmed">
			<button type="submit" name="deleteAll">Yes - do it now</button>
			<button type="button" onclick="window.location = '${returnTo}'">
				No - get me out of here</button>
			<input type="hidden" name="confirmed">
		</form>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>