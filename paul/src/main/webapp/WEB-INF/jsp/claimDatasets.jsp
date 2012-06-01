<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Claim Held Datasets</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>Unclaimed Datasets from '${facilityName}'</h1>
		<form action="claimDatasets" method="post">
			<p>These datasets currently have no designated owner.</p>
			<ul>
				<li><em>Carefully</em> scan the data capture dates and times, and tick
				    the tickbox for entries that match the time you were using the 
				    instrument. Then click the "Claim Datasets" button.</li>
				<li>Please <em>don't claim datasets that were created by someone else.</em>
					It will make it difficult for them to get access to their data.</li>
				<li>If your missing datasets are not listed below, please talk to
					the Lab staff.  The files may have been mistakenly assigned
					to or claimed by the wrong user.</li>
			</ul>
			<c:if test="${!empty message}">
				<div class="alert alert-error">${message}</div>
			</c:if>
			<c:choose>
				<c:when test="${!empty datasets}">
					<table class="table table-striped table-bordered table-condensed">
						<thead>
							<tr>
								<th>Dataset id</th>
								<th>Data write timestamp</th>
								<th>Data capture timestamp</th>
								<th>Base filename</th>
							</tr>
						</thead>
					    <tbody>
						<c:forEach var="dataset" items="${datasets}">
						    <tr>
								<td><input type="checkbox" name="ids" value="${dataset.id}">${dataset.id}</td>
								<td><fmt:formatDate value="${dataset.indicativeFileTimestamp}" 
										type="both" dateStyle="medium"/></td>
								<td><fmt:formatDate value="${dataset.captureTimestamp}" 
										type="both" dateStyle="medium"/></td>
								<td>${dataset.sourceFilePathnameBase}</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
					<input type="hidden" name="facilityName" value="${facilityName}">
					<input type="hidden" name="returnTo" value="${returnTo}">
					<button type="submit" name="claim">Claim Datasets</button>
				</c:when>
				<c:otherwise>
					<div class="alert alert-success">There are no unclaimed datasets meeting your criteria</div>
				</c:otherwise>
			</c:choose>
			<button type="button" onclick="window.location = '/paul'">Cancel</button>
		</form>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>