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
		<ul>
			<li>ACLS Proxy host (i.e. this one) - ${config.proxyHost} :
				${config.proxyPort}</li>
			<li>ACLS Server host - ${config.serverHost} :
				${config.serverPort}</li>
			<li>ACLS Proxy details:
			    <ul>
			   		<li>Allow unknown clients: ${config.allowUnknownClients}</li>
			        <li>Trusted addresses: 
			        	<c:forEach var="addr" items="${config.trustedAddresses}">
			        		${addr} &nbsp;
			        	</c:forEach>
			        </li>
			    	<li>Dummy facility name: ${config.dummyFacilityName}</li>
			    	<li>Dummy facility hostId: ${config.dummyFacilityHostId}</li>
			    </ul>
			</li>
			<li>Base file URL - ${config.baseFileUrl}</li>
			<li>Queue parameters:
				<ul>
					<li>Capture / Queue directory - ${config.captureDirectory}</li>
					<li>Archive directory - ${config.archiveDirectory}</li>
					<li>Queue expiry time - ${config.queueExpiryTime} (minutes)</li>
					<li>Queue expiry interval - ${config.queueExpiryInterval}
						(minutes)</li>
					<li>Expire by deleting - ${config.expireByDeleting}</li>
				</ul>
			</li>
			<li>Data Grabber restart policy -
				${config.dataGrabberRestartPolicy}</li>
			<li>Hold datasets with no user information -
				${config.holdDatasetsWithNoUser}
			<li>Clients use 'project' - ${config.useProject}</li>
			<li>Atom feed parameters for the Ingestion feed:
				<ul>
					<li>Feed URL - ${config.feedUrl}</li>
					<li>Feed id - ${config.feedId}</li>
					<li>Feed title - ${config.feedTitle}</li>
					<li>Feed author - ${config.feedAuthor}</li>
					<li>Feed author email - ${config.feedAuthorEmail}</li>
				</ul>
			</li>
			<li>Links:
			    <ul>
			    	<li>ACLS Booking System - ${config.aclsUrl}</li>
			    	<li>Primary Downstream Repository - ${config.primaryRepositoryUrl}</li>
			    </ul>
			</li>
			<li>Facilities:
				<ul>
					<c:forEach items="${facilities}" var="facility">
						<li><a href="/paul/facilities/${facility.facilityName}">
								${facility.facilityName} - ${facility.facilityDescription} -
								${facility.status} </a></li>
					</c:forEach>
				</ul>
			</li>
		</ul>
		<br>
		<br>
		<form action="config" method="post">
			<button type="submit" name="reset">Reset to default
				configuration settings</button>
		</form>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>