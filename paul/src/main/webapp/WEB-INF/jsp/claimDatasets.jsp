<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Claim Held Datasets</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container">
		<h1>Held Datasets for Instrument '${facilityName}'</h1>
		<form action="claimDatasets" method="post">
			<p>These datasets captured from the '${facilityName}' don't have
				a known owner.
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
			<c:choose>
				<c:when test="${!empty datasets}">
					<table class="table table-striped table-bordered table-condensed">
						<thead>
							<tr>
								<td></td>
								<th>Dataset's queue id #</th>
								<th>Data capture timestamp</th>
								<th>Base filename</th>
							</tr>
						</thead>
					    <tbody>
						<c:forEach var="dataset" items="${datasets}">
						    <tr>
								<td><input type="checkbox" name="id-${dataset.id}"></td>
								<td>${dataset.id}</td>
								<td>${dataset.captureTimestamp}</td>
								<td>${dataset.sourceFilePathnameBase}</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
					<button type="submit" name="claimThem">Claim Datasets</button>
				</c:when>
				<c:otherwise>
					<p>There are no unclaimed datasets</p>
				</c:otherwise>
			</c:choose>
			<button onclick="window.location = '/paul'">Cancel</button>
		</form>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>