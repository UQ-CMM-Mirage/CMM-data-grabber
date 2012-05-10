<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber Queue Deletion</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>Data Grabber Queue Deletion</h1>
		<form class="form-inline btn-toolbar" method="POST" action="${returnTo}">
			Archive files <input id="archive" name="mode" type="radio"
				value="archive" checked="checked"
				onclick="document.form.archive.checked = true; document.form.discard.checked = false;">
			<br> Permanently delete files <input id="discard" name="mode"
				type="radio" value="discard"
				onclick="document.form.archive.checked = false; document.form.discard.checked = true;">
			<br>
			<button class="btn-large" type="submit" name="deleteAll">Yes - do it now</button>
			<button class="btn-large" type="button" onclick="window.location = '${returnTo}'">
				No - get me out of here</button>
			<input type="hidden" name="confirmed">
		</form>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>