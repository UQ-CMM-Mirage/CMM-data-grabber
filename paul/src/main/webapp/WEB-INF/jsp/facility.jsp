<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
<title>Facility Configuration / Controls for
	${facility.facilityName}</title>
</head>
<body>
	<h1>Facility Configuration / Controls for ${facility.facilityName}</h1>
	<ul>
		<li>DNS name / IP address - ${facility.address}</li>
		<li>Drive name - ${facility.driveName}</li>
		<li>Folder name - ${facility.folderName}</li>
		<li>Access name - ${facility.accessName}</li>
		<li>Access password - ${facility.accessPassword}</li>
		<li>Case insensitive datafile matching -
			${facility.caseInsensitive}</li>
		<li>Datafile templates: ${empty facility.datafileTemplates ?
			'none' : ''}
			<ul>
				<c:forEach items="${facility.datafileTemplates}" var="template">
					<li>Pattern - '${template.filePattern}', mimeType -
						'${template.mimeType}', suffix - '${template.suffix}', optional -
						${template.optional}</li>
				</c:forEach>
			</ul>
		</li>
		<li>File settling time - ${facility.fileSettlingTime} ms</li>
		<li>Uses file locking - ${facility.useFileLocks}</li>
		<li>Client uses timer - ${facility.useTimer}</li>
		<li>Client uses full screen - ${facility.useFullScreen}</li>
		<li>Facility status - ${facility.status}</li>
		<li>Facility configuration diagnostic - ${facility.message}</li>
		<li>Dummy facility - ${facility.dummy}</li>
	</ul>
</body>
</html>