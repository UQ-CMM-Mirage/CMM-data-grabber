<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber - Facility Configuration 
	<c:if test="${! empty facility.facilityName}"> 
		for ${facility.facilityName}
	</c:if>
	</title>
<script type="text/javascript">
<!--
  function addTemplate(position) {
	var i = parseInt(position) + 1;
	var html = [
	    '<tr id="template' + i + '"><td>&nbsp;</td>' +
		'<td colspan="3">Template #' + i + '</td>' +
		'<td><button type="button" onclick="addTemplate(' + i + ')">' +
		'Add</button></td>' +
		'<button type="button" onclick="removeTemplate(' + i + ')">' +
		'Remove</button></td>' +
        '</tr>',
		'<tr><td>&nbsp;</td><td>&nbsp;</td>' +
		'<td>File pattern</td>' +
		'<td><input name="template' + i + 'filePattern" type="text" class="span4"></td>' +
		'<td></td>' +
		'</tr>',
		'<tr><td>&nbsp;</td><td>&nbsp;</td>' +
		'<td>File mimeType</td>' +
		'<td><input name="template' + i + 'mimeType" type="text" class="span4"></td>' +
		'<td></td>' +
		'</tr>',
		'<tr><td>&nbsp;</td><td>&nbsp;</td>' +
		'<td>File suffix</td>' +
		'<td><input name="template' + i + 'suffix" type="text" class="span4"</td>' +
		'<td></td>' +
		'</tr>',
		'<tr><td>&nbsp;</td><td>&nbsp;</td>' +
		'<td>File is optional</td>' +
		'<td><input name="template' + i + 'optional" type="checkbox" checked="checked" value="true"></td>' +
		'<td></td>' +
		'</tr>'
	];
	var before = document.getElementById('template' + (position + 1));
	if (before) {
		var tbody = before.parentNode;
		var templateNo = null;
		var j;
		for (j in tbody.childNodes) {
			var tr = tbody.childNodes[j];
			if (tr.nodeType != Node.ELEMENT_NODE) continue;
			if (tr.hasAttribute('id')) {
				var id = tr.getAttribute('id');
				var match = id.match(/template(\d+)/);
				if (!(match && parseInt(match[1]) > position)) continue;
				templateNo = parseInt(match[1]) + 1;
				tr.setAttribute('id', 'template' + templateNo);
				var k;
				for (k = 0; k < tr.childNodes.length; k++) {
					var td = tr.childNodes[k];
					if (td.nodeType != Node.ELEMENT_NODE) continue;
					var content = td.childNodes[0].textContent;
					if (content) {
						if (content.match(/Template #\d+/)) {
							td.childNodes[0].textContent = "Template #" + templateNo;
							break;
						}
					}
				}
				var buttons = tr.getElementsByTagName('button');
				if (buttons) {
					for (k = 0; k < buttons.length; k++) {
						var button = buttons[k];
						var onClick = button.getAttribute('onClick');
						if (!onClick) continue;
						if (onClick.match(/addTemplate\(\d+\)/)) {
							button.setAttribute('onClick', 'addTemplate(' + templateNo + ')');
						}
						if (onClick.match(/removeTemplate\(\d+\)/)) {
							button.setAttribute('onClick', 'removeTemplate(' + templateNo + ')');
						}
					}
				}
			} else {
				if (templateNo) {
					var inputs = tr.getElementsByTagName('input');
					if (inputs) {
						var j;
						for (j = 0; j < inputs.length; j++) {
							var input = inputs[j];
							var name = input.getAttribute('name');
							if (name) {
								var match = name.match(/template(\d+)(.+)/);
								if (match) {
									input.setAttribute('name', 'template' + templateNo + match[2]);
								}
							}
						}
					}
				}
			}
		}
	} else {
		before = document.getElementById('templateEnd');
	}
	for (i in html) {
		var elem = document.createElement('tbody');
		elem.innerHTML = html[i];
		var node = elem.childNodes[0];
		before.parentNode.insertBefore(node, before);
	}
	var lastInput = document.getElementById('lastTemplate');
	if (lastInput) {
		var lastValue = lastInput.getAttribute('value');
		lastInput.setAttribute('value', parseInt(lastValue) + 1);
	}
  }
  
  function removeTemplate(position) {
	  var templateNode = document.getElementById('template' + position);
	  if (templateNode) {
		  var i = 0;
		  while (i < 4) {
			  var next = templateNode.nextSibling;
			  if (next.nodeType == Node.ELEMENT_NODE) i++;
			  templateNode.parentNode.removeChild(next);
		  }
		  templateNode.parentNode.removeChild(templateNode);
	  }
  }
-->
</script>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>Facility Configuration
			<c:if test="${! empty facility.facilityName}"> 
				for ${facility.facilityName}
			</c:if>
		</h1>
		<p><strong>${message}</strong></p>
		<form action="${facility.facilityName}" method="post">
			<input id="lastTemplate" type="hidden" name="lastTemplate"
				value="${fn:length(facility.datafileTemplates)}">
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
							${edit ? '' : 'readonly="readonly"
							'}
						value="${facility.facilityName}"></td>
						<td><strong>${diags.facilityName}</strong></td>
					</tr>
					<tr>
						<td colspan="3">Facility description</td>
						<td><input name="facilityDescription" type="text"
							class="span4" ${edit ? '' : 'readonly="readonly"
							'}
						value="${facility.facilityDescription}"></td>
						<td><strong>${diags.facilityDescription}</strong></td>
					</tr>
					<tr>
						<td colspan="3">DNS name / IP address</td>
						<td><input name="address" type="text" class="span4"
							${edit ? '' : 'readonly="readonly"
							'}
						value="${facility.address}"></td>
						<td><strong>${diags.address}</strong></td>
					</tr>
					<tr>
						<td colspan="3">Local Host ID</td>
						<td><input name="localHostId" type="text" class="span4"
							${edit ? '' : 'readonly="readonly"
							'}
						value="${facility.localHostId}"></td>
						<td><strong>${diags.localHostId}</strong></td>
					</tr>
					<tr>
						<td colspan="3">Drive name</td>
						<td><input name="driveName" type="text" class="span4"
							${edit ? '' : 'readonly="readonly"
							'}
						value="${facility.driveName}"></td>
						<td><strong>${diags.driveName}</strong></td>
					</tr>
					<tr>
						<td colspan="3">Folder name</td>
						<td><input name="folderName" type="text" class="span4"
							${edit ? '' : 'readonly="readonly"
							'}
						value="${facility.folderName}"></td>
						<td><strong>${diags.folderName}</strong></td>
					</tr>
					<tr>
						<td colspan="3">Access name</td>
						<td><input name="accessName" type="text" class="span4"
							${edit ? '' : 'readonly="readonly"
							'}
						value="${facility.accessName}"></td>
						<td><strong>${diags.accessName}</strong></td>
					</tr>
					<tr>
						<td colspan="3">Access password</td>
						<td><input name="accessPassword" type="text" class="span4"
							${edit ? '' : 'readonly="readonly"
							'}
						value="${facility.accessPassword}"></td>
						<td><strong>${diags.accessPassword}</strong></td>
					</tr>
					<tr>
						<td colspan="3">Case insensitive datafile matching</td>
						<td><input name="caseInsensitive" type="checkbox"
							${edit ? ' ' : ' readonly="readonly"
							'}
						${facility.caseInsensitive ? ' checked="checked"
							' : ' '}
						value="true"></td>
						<td><strong>${diags.caseInsensitive}</strong></td>
					</tr>
					<tr id="templateStart">
						<td colspan="3">Datafile templates</td>
						<td>${(!edit && empty facility.datafileTemplates) ? 'none' :
							''}</td>
						<td><c:if test="${edit}">
								<button type="button" onclick="addTemplate(0)">Add</button>
							</c:if></td>
					</tr>
					<c:set var="index" value="1" />
					<c:forEach items="${facility.datafileTemplates}" var="template">
						<tr id="template${index}">
							<td>&nbsp;</td>
							<td colspan="3">Template #${index}</td>
							<td><c:if test="${edit}">
									<button type="button" onclick="addTemplate(${index})">Add</button>
									<button type="button" onclick="removeTemplate(${index})">Remove</button>
								</c:if></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>File pattern</td>
							<td><input name="template${index}filePattern" type="text"
								class="span4" ${edit ? '' : 'readonly="readonly"
								'}
								value="${template.filePattern}"></td>
							<td><c:set var="key" value="template${index}filePattern" />
								<strong>${diags[key]}</strong></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>File mimeType</td>
							<td><input name="template${index}mimeType" type="text"
								class="span4" ${edit ? '' : 'readonly="readonly"
								'}
								value="${template.mimeType}"></td>
							<td><c:set var="key" value="template${index}mimeType" /> <strong>${diags[key]}</strong>
							</td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>File suffix</td>
							<td><input name="template${index}suffix" type="text"
								class="span4" ${edit ? '' : 'readonly="readonly"
								'}
								value="${template.suffix}"></td>
							<td><c:set var="key" value="template${index}suffix" /> <strong>${diags[key]}</strong>
							</td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>File is optional</td>
							<td><input name="template${index}optional" type="checkbox"
								${edit ? ' ' : ' readonly="readonly"
								'}
							${template.optional ? ' checked="checked"
								' : ' '}
							value="true"></td>
							<td><c:set var="key" value="template${index}optional" /> <strong>${diags[key]}</strong>
							</td>
						</tr>
						<c:set var="index" value="${index + 1}" />
					</c:forEach>
					<tr id="templateEnd">
						<td colspan="3">File settling time (milliseconds)</td>
						<td><input name="fileSettlingTime" type="text" class="span4"
							${edit ? '' : 'readonly="readonly"
							'}
						value="${facility.fileSettlingTime}"></td>
						<td><strong>${diags.fileSettlingTime}</strong></td>
					</tr>
					<tr>
						<td colspan="3">Uses file locking</td>
						<td><input name="useFileLocks" type="checkbox"
							${edit ? ' ' : ' readonly="readonly"
							'}
						${facility.useFileLocks ? ' checked="checked"
							' : ' '}
						value="true"></td>
						<td><strong>${diags.useFileLocks}</strong></td>
					</tr>
					<tr>
						<td colspan="3">Client uses timer</td>
						<td><input name="useTimer" type="checkbox"
							${edit ? ' ' : ' readonly="readonly"
							'}
						${facility.useTimer ? ' checked="checked"
							' : ' '}
						value="true"></td>
						<td><strong>${diags.useTimer}</strong></td>
					</tr>
					<tr>
						<td colspan="3">Client uses full screen</td>
						<td><input name="useFullScreen" type="checkbox"
							${edit ? ' ' : ' readonly="readonly"
							'}
						${facility.useFullScreen ? ' checked="checked"
							' : ' '}
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
			<c:choose>
				<c:when test="${!empty create}">
					<button class="btn" type="submit" name="create">Save New Facility</button>
				</c:when>
				<c:when test="${!empty edit}">
					<button class="btn" type="submit" name="update">Save Facility Changes</button>
				</c:when>
			</c:choose>
		</form>
		<c:if test="${empty edit}">
			<div class="form-inline">
				<form class="form-inline" action="${facility.facilityName}">
					<button class="btn" type="submit" name="edit">Change
						Facility Configuration</button>
				</form>
			</div>
		</c:if>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>