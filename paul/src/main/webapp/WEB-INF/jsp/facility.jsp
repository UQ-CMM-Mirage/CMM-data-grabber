<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber - Facility Configuration for ${facility.facilityName}</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>Facility Configuration for ${facility.facilityName}</h1>
		<table class="table table-striped table-condensed">
			<thead>
				<tr>
					<th colspan="3">Property</th>
					<th>Value</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td colspan="3">DNS name / IP address</td>
					<td>${facility.address}</td>
				</tr>
			    <tr>
					<td colspan="3">Local Host ID</td>
					<td>${facility.localHostId}</td>
				</tr>
			    <tr>
					<td colspan="3">Drive name</td>
					<td>${facility.driveName}</td>
				</tr>
			    <tr>
					<td colspan="3">Folder name</td>
					<td>${facility.folderName}</td>
				</tr>
			    <tr>
					<td colspan="3">Access name</td>
					<td>${facility.accessName}</td>
				</tr>
			    <tr>
					<td colspan="3">Access password</td>
					<td>${facility.accessPassword}</td>
				</tr>
			    <tr>
					<td colspan="3">Case insensitive datafile matching</td>
					<td>${facility.caseInsensitive}</td>
				</tr>
			    <tr>
					<td colspan="3">Datafile templates</td>
					<td>${empty facility.datafileTemplates ? 'none' : ''}</td>
				</tr>
				<c:forEach items="${facility.datafileTemplates}" var="template">
				    <tr>
					    <td>&nbsp;</td>
						<td colspan="3">Template #i</td>
					</tr>
					<tr>
					    <td>&nbsp;</td><td>&nbsp;</td>
						<td>Pathname pattern</td><td>${template.filePattern}</td>
					</tr>
					<tr>
						<td>&nbsp;</td><td>&nbsp;</td>
						<td>File mimeType</td><td>${template.mimeType}</td>
					</tr>
					<tr>
						<td>&nbsp;</td><td>&nbsp;</td>
						<td>File suffix</td><td>${template.suffix}</td>
					</tr>
					<tr>
						<td>&nbsp;</td><td>&nbsp;</td>
						<td>File is optional</td><td>${template.optional}</td>
					</tr>
				</c:forEach>
			    <tr>
					<td colspan="3">File settling time</td>
					<td>${facility.fileSettlingTime} ms</td>
				</tr>
			    <tr>
					<td colspan="3">Uses file locking</td>
					<td>${facility.useFileLocks}</td>
				</tr>
			    <tr>
					<td colspan="3">Client uses timer</td>
					<td>${facility.useTimer}</td>
				</tr>
			    <tr>
					<td colspan="3">Client uses full screen</td>
					<td>${facility.useFullScreen}</td>
				</tr>
			    <tr>
					<td colspan="3">Facility status</td>
					<td>${facility.status}</td>
				</tr>
			    <tr>
					<td colspan="3">Facility configuration diagnostic</td>
					<td>${facility.message}</td>
			    </tr>
			</tbody>
	    </table>
		<c:if test="${facility.status == 'ON'}">
			<form action="${facility.facilityName}" method="post">
				<button type="submit" name="disableWatcher">Disable File
					Watching</button>
			</form>
		</c:if>
		<c:if
			test="${facility.status == 'DISABLED' || facility.status == 'OFF'}">
			<form action="${facility.facilityName}" method="post">
				<button type="submit" name="enableWatcher">Enable File
					Watching</button>
			</form>
		</c:if>
		<form action="${facility.facilityName}">
			<button type="submit" name="sessionLog">Session Log</button>
		</form>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>