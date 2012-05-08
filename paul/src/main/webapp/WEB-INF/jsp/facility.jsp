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
		<p>
			${message}
		</p>
		<form action="${facility.facilityName}" method="post">
		<table class="table table-striped table-condensed">
			<thead>
				<tr>
					<th colspan="3" class="span4">Property</th>
					<th class="span4">Value</th>
					<th class="span4"></th>
				</tr>
			</thead>
			<tbody class="form-inline">
				<tr>
					<td colspan="3">Facility name</td>
					<td><input name="facilityName" type="text" class="span4"
						${edit ? '' : 'readonly="readonly"'}
						value="${facility.facilityName}"></td>
					<td><strong>${diags.facilityName}</strong></td>
				</tr>
				<tr>
					<td colspan="3">Facility description</td>
					<td><input name="facilityDescription" type="text" class="span4"
						${edit ? '' : 'readonly="readonly"'}
						value="${facility.facilityDescription}"></td>
					<td><strong>${diags.facilityDescription}</strong></td>
				</tr>
				<tr>
					<td colspan="3">DNS name / IP address</td>
					<td><input name="address" type="text"  class="span4"
						${edit ? '' : 'readonly="readonly"'}
						value="${facility.address}"></td>
					<td><strong>${diags.address}</strong></td>
				</tr>
			    <tr>
					<td colspan="3">Local Host ID</td>
					<td><input name="localHostId" type="text" class="span4"
						${edit ? '' : 'readonly="readonly"'}
						value="${facility.localHostId}"></td>
					<td><strong>${diags.localHostId}</strong></td>
				</tr>
			    <tr>
					<td colspan="3">Drive name</td>
					<td><input name="driveName" type="text" class="span4"
						${edit ? '' : 'readonly="readonly"'}
						value="${facility.driveName}"></td>
					<td><strong>${diags.driveName}</strong></td>
				</tr>
			    <tr>
					<td colspan="3">Folder name</td>
					<td><input name="folderName" type="text" class="span4"
						${edit ? '' : 'readonly="readonly"'}
						value="${facility.folderName}"></td>
					<td><strong>${diags.folderName}</strong></td>
				</tr>
			    <tr>
					<td colspan="3">Access name</td>
					<td><input name="accessName" type="text" class="span4"
						${edit ? '' : 'readonly="readonly"'}
						value="${facility.accessName}"></td>
					<td><strong>${diags.accessName}</strong></td>
				</tr>
			    <tr>
					<td colspan="3">Access password</td>
					<td><input name="accessPassword" type="text" class="span4"
						${edit ? '' : 'readonly="readonly"'}
						value="${facility.accessPassword}"></td>
					<td><strong>${diags.accessPassword}</strong></td>
				</tr>
			    <tr>
					<td colspan="3">Case insensitive datafile matching</td>
					<td><input name="caseInsensitive" type="checkbox"
						${edit ? ' ' : ' readonly="readonly" '}
						${facility.caseInsensitive ? ' checked="checked" ' : ' '}
						value="true"></td>
					<td><strong>${diags.caseInsensitive}</strong></td>
				</tr>
			    <tr>
					<td colspan="3">Datafile templates</td>
					<td>${empty facility.datafileTemplates ? 'none' : ''}</td>
					<td></td>
				</tr>
				<c:set var="index" value="1"/>
				<c:forEach items="${facility.datafileTemplates}" var="template">
				    <tr>
					    <td>&nbsp;</td>
						<td colspan="3">Template #${index}</td>
						<td></td>
					</tr>
					<tr>
					    <td>&nbsp;</td><td>&nbsp;</td>
						<td>File pattern</td>
						<td><input name="template${index}filePattern" type="text" class="span4"
								${edit ? '' : 'readonly="readonly"'}
								value="${template.filePattern}"></td>
						<td>
							<c:set var="key" value="template${index}filePattern"/>
							<strong>${diags[key]}</strong>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td><td>&nbsp;</td>
						<td>File mimeType</td>
						<td><input name="template${index}mimeType" type="text" class="span4"
								${edit ? '' : 'readonly="readonly"'}
								value="${template.mimeType}"></td>
						<td>
							<c:set var="key" value="template${index}mimeType"/>
							<strong>${diags[key]}</strong>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td><td>&nbsp;</td>
						<td>File suffix</td>
						<td><input name="template${index}suffix" type="text" class="span4"
								${edit ? '' : 'readonly="readonly"'}
								value="${template.suffix}"></td>
						<td>
							<c:set var="key" value="template${index}suffix"/>
							<strong>${diags[key]}</strong>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td><td>&nbsp;</td>
						<td>File is optional</td>
						<td><input name="template${index}optional" type="checkbox"
							${edit ? ' ' : ' readonly="readonly" '}
							${template.optional ? ' checked="checked" ' : ' '}
							value="true"></td>
						<td>
							<c:set var="key" value="template${index}optional"/>
							<strong>${diags[key]}</strong>
						</td>
					</tr>
					<c:set var="index" value="${index + 1}"/>
				</c:forEach>
			    <tr>
					<td colspan="3">File settling time (milliseconds)</td>
					<td><input name="fileSettlingTime" type="text" class="span4"
						${edit ? '' : 'readonly="readonly"'}
						value="${facility.fileSettlingTime}"></td>
					<td><strong>${diags.fileSettlingTime}</strong></td>
				</tr>
			    <tr>
					<td colspan="3">Uses file locking</td>
					<td><input name="useFileLocks" type="checkbox"
						${edit ? ' ' : ' readonly="readonly" '}
						${facility.useFileLocks ? ' checked="checked" ' : ' '}
						value="true"></td>
					<td><strong>${diags.useFileLocks}</strong></td>
				</tr>
			    <tr>
					<td colspan="3">Client uses timer</td>
					<td><input name="useTimer" type="checkbox"
						${edit ? ' ' : ' readonly="readonly" '}
						${facility.useTimer ? ' checked="checked" ' : ' '}
						value="true"></td>
					<td><strong>${diags.useTimer}</strong></td>
				</tr>
			    <tr>
					<td colspan="3">Client uses full screen</td>
					<td><input name="useFullScreen" type="checkbox"
						${edit ? ' ' : ' readonly="readonly" '}
						${facility.useFullScreen ? ' checked="checked" ' : ' '}
						value="true"></td>
					<td><strong>${diags.useFullScreen}</strong></td>
				</tr>
			    <tr>
					<td colspan="3">Facility status</td>
					<td>${facility.status}</td>
					<td></td>
				</tr>
			    <tr>
					<td colspan="3">Facility configuration diagnostic</td>
					<td>${facility.message}</td>
					<td></td>
			    </tr>
			</tbody>
	    </table>
	    <c:if test="${!empty edit}">
			<button type="submit" name="update">Save Configuration</button>
		</c:if>
		</form>
		<c:if test="${empty edit}">
			<form action="${facility.facilityName}" method="get">
				<button type="submit" name="edit">Edit Configuration</button>
			</form>
		</c:if>
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
			<button type="submit" name="sessionLog">View Session Log</button>
		</form>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>