<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber Confirmation</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>Data Grabber Dataset Management Confirmation</h1>
		<form class="form-inline btn-toolbar" method="POST" action="manageDatasets">
		    <div class="alert alert-info">
		        Confirmation is required - do you <em>really</em> want to
				${discard ? 'Permanently Delete' : 'Archive'}
				${slice == 'ALL' ? 'All' : slice == 'HELD' ? 'All Held' : 'All Ingestible' }
				Datasets <c:if test="${! empty facilityName}">for ${facilityName}</c:if>?
			</div>
			<br>
			<button class="btn-large btn-danger" type="submit" name="action" 
				value="${discard ? 'deleteAll' : 'archiveAll'}">Yes - do it now</button>
			<button class="btn-large" type="button" onclick="window.location = '${returnTo}'">
				No - get me out of here</button>
			<input type="hidden" name="confirmed">
			<input type="hidden" name="slice" value="${slice}">
			<input type="hidden" name="facilityName" value="${facilityName}">
		</form>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>