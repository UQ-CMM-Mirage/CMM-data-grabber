<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
<title>Data Grabber Queue Entry</title>
<link href="/paul/css/ui-lightness/jquery-ui-1.8.18.custom.css"
	rel="stylesheet" type="text/css">
<script type="text/javascript" src="/paul/js/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="/paul/js/jquery-ui-1.8.18.custom.min.js"></script>
<script>
	$(function() {
		var availableTags = [
            <c:forEach items="${userNames}" var="userName">'${userName}',</c:forEach> 
		];
		$( "#users" ).autocomplete({
			source: availableTags
		});
	});
</script>
</head>
<body class=".ui-widget">
	<h1 class=".ui-widget-header">Data Grabber Queue Entry # ${entry.id}</h1>
	<div class=".ui-widget-content">
	<ul>
		<li>Facility : ${entry.facilityName}</li>
		<li>User : ${entry.userName}</li>
		<li>Account : ${entry.accountName}</li>
		<li>Email : ${entry.emailAddress}</li>
		<li>Captured: ${entry.captureTimestamp}</li>
		<li>Metadata updated: ${entry.updateTimestamp}</li>
		<li>Session uuid: ${entry.sessionUuid}</li>
		<li>Session start time: ${entry.sessionStartTimestamp}</li>
		<li>Dataset uuid: ${entry.recordUuid}</li>
		<li>Dataset base filename: ${entry.sourceFilePathnameBase}</li>
		<li>Datafiles:
			<ul>
				<c:forEach items="${entry.datafiles}" var="datafile">
					<li>Filename: ${datafile.sourceFilePathname}
						<ul>
							<li>File id: ${datafile.id}</li>
							<li>Captured filename: ${datafile.capturedFilePathname}</li>
							<li>File modified: ${datafile.fileWriteTimestamp}</li>
							<li>File captured: ${datafile.captureTimestamp}</li>
							<li>Mime type: ${datafile.mimeType}</li>
						</ul>
					</li>
				</c:forEach>
			</ul>
		</li>
		<li>Metadata filename: ${entry.metadataFilePathname}</li>
	</ul>
	<c:if test="${empty entry.userName}">
			<form action="${entry.id}" method="POST">
				User name: <input type="text" name="userName" id="users"> <br>
				<button type="submit" name="claim">Claim as mine</button>
				<button type="submit" name="assign">Assign to user</button>
			</form>
	</c:if>
	</div>
</body>
</html>