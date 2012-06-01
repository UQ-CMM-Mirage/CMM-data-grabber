<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Manage Queued Datasets</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>
		    ${slice == 'ALL' ? 'All' : slice == 'HELD' ? 'Held' : 'Ingestible' }
		    Datasets for Instrument '${facilityName}'
		</h1>
		<form action="manageDatasets" method="post">
			<c:if test="${!empty message}">
				<div class="alert alert-error">${message}</div>
			</c:if>
			<c:choose>
				<c:when test="${!empty datasets}">
					<table class="table table-striped table-bordered table-condensed">
						<thead>
							<tr>
								<td class="span1"></td>
								<th class="span2">Dataset's queue id #</th>
								<th class="span2">Current owner</th>
								<th class="span2">Data capture timestamp</th>
								<th class="span5">Base filename</th>
							</tr>
						</thead>
					    <tbody>
						<c:forEach var="dataset" items="${datasets}">
						    <tr>
								<td><input type="checkbox" name="ids" value="${dataset.id}"></td>
								<td>${dataset.id}</td>
								<td>${dataset.userName}</td>
								<td><fmt:formatDate value="${dataset.captureTimestamp}" 
										type="both" dateStyle="medium"/></td>
								<td>${dataset.sourceFilePathnameBase}</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
					<input type="hidden" name="facilityName" value="${facilityName}">
					<input type="hidden" name="slice" value="${slice}">
					<input type="hidden" name="returnTo" value="${returnTo}">
					<button type="submit" name="action" value="assign">Assign Datasets to</button>
					<input type="text" name="userName">
					<button type="submit" name="action" value="delete">Delete Datasets</button>
					<button type="submit" name="action" value="archive">Archive Datasets</button>
				</c:when>
				<c:otherwise>
					<div class="alert alert-success">There are no datasets meeting your criteria</div>
				</c:otherwise>
			</c:choose>
			<button type="button" onclick="window.location = '${returnTo}'">Cancel</button>
		</form>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>