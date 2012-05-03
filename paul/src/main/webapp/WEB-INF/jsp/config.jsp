<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber Configuration</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>Data Grabber Configuration</h1>
		<table class="table table-striped table-condensed">
			<thead>
				<tr>
					<th colspan="2">Property</th>
					<th>Value</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td colspan="2">ACLS Proxy host</td>
					<td>${config.proxyHost}</td>
				</tr>
				<tr>
					<td colspan="2">ACLS Proxy port</td>
					<td>${config.proxyPort}</td>
				</tr>
				<tr>
					<td colspan="2">ACLS Server host</td>
					<td>${config.serverHost}</td>
				</tr>
				<tr>
					<td colspan="2">ACLS Server port</td>
					<td>${config.serverPort}</td>
				</tr>
				<tr>
					<td colspan="3">ACLS Proxy details:</td>
				</tr>
				<tr>
					<td></td>
					<td>Allow unknown clients</td>
					<td>${config.allowUnknownClients}</td>
				</tr>
				<tr>
					<td></td>
					<td>Trusted addresses</td>
					<td><c:forEach var="addr" items="${config.trustedAddresses}">
			        		${addr} &nbsp;
			        	</c:forEach></td>
				</tr>
				<tr>
					<td></td>
					<td>Dummy facility name</td>
					<td>${config.dummyFacilityName}</td>
				</tr>
				<tr>
					<td></td>
					<td>Dummy facility hostId</td>
					<td>${config.dummyFacilityHostId}</td>
					<td>
				<tr>
					<td colspan="2">Base file URL</td>
					<td>${config.baseFileUrl}</td>
				<tr>
					<td colspan="3">Queue parameters:</td>
				</tr>
				<tr>
					<td></td>
					<td>Capture / Queue directory</td>
					<td>${config.captureDirectory}</td>
				</tr>
				<tr>
					<td></td>
					<td>Archive directory</td>
					<td>${config.archiveDirectory}</td>
				</tr>
				<tr>
					<td></td>
					<td>Queue expiry time</td>
					<td>${config.queueExpiryTime} (minutes)</td>
				</tr>
				<tr>
					<td></td>
					<td>Queue expiry interval</td>
					<td>${config.queueExpiryInterval} (minutes)</td>
				</tr>
				<tr>
					<td></td>
					<td>Expire by deleting</td>
					<td>${config.expireByDeleting}</td>
				</tr>
				<tr>
					<td colspan="2">Data Grabber restart policy</td>
					<td>${config.dataGrabberRestartPolicy}</td>
				</tr>
				<tr>
					<td colspan="2">Hold datasets with no user information</td>
					<td>${config.holdDatasetsWithNoUser}</td>
				</tr>
				<tr>
					<td colspan="2">Clients use 'project'</td>
					<td>${config.useProject}</td>
				</tr>
				<tr><td colspan="3">Atom parameters for the Ingestion feed:</td></tr>
				<tr>
					<td></td>
					<td>Feed URL</td>
					<td>${config.feedUrl}
				</tr>
				<tr>
					<td></td>
					<td>Feed id</td>
					<td>${config.feedId}
				</tr>
				<tr>
					<td></td>
					<td>Feed title</td>
					<td>${config.feedTitle}
				</tr>
				<tr>
					<td></td>
					<td>Feed author</td>
					<td>${config.feedAuthor}
				</tr>
				<tr>
					<td></td>
					<td>Feed author email</td>
					<td>${config.feedAuthorEmail}</td>
				</tr>
				<tr><td colspan="3">Links:</td></tr>
			    <tr>
					<td></td>
					<td>ACLS Booking System</td>
					<td>${config.aclsUrl}</td>
				</tr>
			    <tr>
					<td></td>
					<td>Primary Downstream Repository</td>
					<td>${config.primaryRepositoryUrl}</td>
				</tr>
			<!--
			<li>Facilities:
				<ul>
					<c:forEach items="${facilities}" var="facility">
						<li><a href="/paul/facilities/${facility.facilityName}">
								${facility.facilityName} - ${facility.facilityDescription} -
								${facility.status} </a></li>
					</c:forEach>
				</ul>
			</li>
			-->
			</tbody>
		</table>
		<form action="config" method="post">
			<button type="submit" name="reset">Reset to default
				configuration settings</button>
		</form>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>