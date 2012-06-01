<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag" %>
<title>Data Grabber Queued Dataset</title>
<link href="/paul/css/ui-lightness/jquery-ui-1.8.18.custom.css"
	rel="stylesheet" type="text/css">
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
<body>
    <%@ include file="/WEB-INF/jsp/commonHeader.jspFrag" %>
	<div class="container-fluid">
	<h1>Data Grabber Queued Dataset # ${entry.id}</h1>
	<div class=".ui-widget-content">
	<ul>
		<li>Facility : ${entry.facilityName}</li>
		<li>User : ${entry.userName}</li>
		<li>Account : ${entry.accountName}</li>
		<li>Email : ${entry.emailAddress}</li>
		<li>Captured: <fmt:formatDate value="${entry.captureTimestamp}" 
										type="both" dateStyle="medium"/></li>
		<li>Metadata updated: <fmt:formatDate value="${entry.updateTimestamp}" 
										type="both" dateStyle="medium"/></li>
		<li>Session uuid: ${entry.sessionUuid}</li>
		<li>Session start time: <fmt:formatDate value="${entry.sessionStartTimestamp}" 
										type="both" dateStyle="medium"/></li>
		<li>Dataset uuid: ${entry.recordUuid}</li>
		<li>Dataset base filename: ${entry.sourceFilePathnameBase} / ${entry.facilityFilePathnameBase}</li>
		<li>Datafiles:
			<ul>
				<c:forEach items="${entry.datafiles}" var="datafile">
					<li>Filename: ${datafile.sourceFilePathname} / ${datafile.facilityFilePathname}
						<ul>
							<li>File id: ${datafile.id}</li>
							<li>Captured filename: ${datafile.capturedFilePathname}</li>
							<li>File modified: 
								<fmt:formatDate value="${datafile.fileWriteTimestamp}" 
										type="both" dateStyle="medium"/></li>
							<li>File captured:
								<fmt:formatDate value="${datafile.captureTimestamp}" 
										type="both" dateStyle="medium"/></li>
							<li>Mime type: ${datafile.mimeType}</li>
						</ul>
					</li>
				</c:forEach>
			</ul>
		</li>
		<li>Metadata filename: ${entry.metadataFilePathname}</li>
	</ul>
	<button class="btn btn-small" type="button" onclick="history.back()">OK</button>
	</div>
	</div><!-- /container -->
<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag" %>
<script type="text/javascript" src="/paul/js/jquery-ui-1.8.18.custom.min.js"></script>
</body>
</html>