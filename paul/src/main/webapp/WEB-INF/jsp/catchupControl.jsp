<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber Catchup Control</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>Catchup Control for ${facilityName}</h1>
		<p>
			Grabber status : ${status.status} <br> Grabber message :
			${status.message} <br> Grabber HWM timestamp :
			${status.grabberHWMTimestamp} <br> Timestamp of last queued
			Dataset : ${catchupTimestamp}
		</p>
		<h2>Statistics</h2>
		<table class="table table-striped table-condensed">
			<thead>
				<tr><th class="span2">Timespan</th>
					<th class="span2">All Datasets on S:</th>
					<th class="span2">Duplicate Datasets on S:</th>
					<th class="span2">All Datasets in queues</th>
					<th class="span2">Duplicate Datasets in queues</th>
					<th class="span2">Datasets in both places</th>
					<th class="span2">Datasets missing from S:</th>
					<th class="span2">Datasets missing from Queues</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>all</td>
					<td>${analysis.all.totalInFolder}</td>
					<td>${analysis.all.multipleInFolder}</td>
					<td>${analysis.all.totalInDatabase}</td>
					<td>${analysis.all.multipleInDatabase}</td>
					<td>${analysis.all.totalMatching}</td>
					<td>${analysis.all.missingFromFolder.size}</td>
					<td>${analysis.all.missingFromDatabase.size}</td>
				</tr>
				<c:if test="${!empty analysis.beforeHWM}">
					<tr>
						<td>before HWM</td>
						<td>${analysis.beforeHWM.totalInFolder}</td>
						<td>${analysis.beforeHWM.multipleInFolder}</td>
						<td>${analysis.beforeHWM.totalInDatabase}</td>
						<td>${analysis.beforeHWM.multipleInDatabase}</td>
						<td>${analysis.beforeHWM.totalMatching}</td>
						<td>${analysis.beforeHWM.missingFromFolder.size}</td>
						<td>${analysis.beforeHWM.missingFromDatabase.size}</td>
					</tr>
				</c:if>
				<c:if test="${!empty analysis.afterHWM}">
					<tr>
						<td>after HWM</td>
						<td>${analysis.afterHWM.totalInFolder}</td>
						<td>${analysis.afterHWM.multipleInFolder}</td>
						<td>${analysis.afterHWM.totalInDatabase}</td>
						<td>${analysis.afterHWM.multipleInDatabase}</td>
						<td>${analysis.afterHWM.totalMatching}</td>
						<td>${analysis.afterHWM.missingFromFolder.size}</td>
						<td>${analysis.afterHWM.missingFromDatabase.size}</td>
					</tr>
				</c:if>
				<c:if test="${!empty analysis.beforeQEnd}">
					<tr>
						<td>before current Queue End</td>
						<td>${analysis.beforeQEnd.totalInFolder}</td>
						<td>${analysis.beforeQEnd.multipleInFolder}</td>
						<td>${analysis.beforeQEnd.totalInDatabase}</td>
						<td>${analysis.beforeQEnd.multipleInDatabase}</td>
						<td>${analysis.beforeQEnd.totalMatching}</td>
						<td>${analysis.beforeQEnd.missingFromFolder.size}</td>
						<td>${analysis.beforeQEnd.missingFromDatabase.size}</td>
					</tr>
				</c:if>
				<c:if test="${!empty analysis.afterQEnd}">
					<tr>
						<td>after current Queue End</td>
						<td>${analysis.afterQEnd.totalInFolder}</td>
						<td>${analysis.afterQEnd.multipleInFolder}</td>
						<td>${analysis.afterQEnd.totalInDatabase}</td>
						<td>${analysis.afterQEnd.multipleInDatabase}</td>
						<td>${analysis.afterQEnd.totalMatching}</td>
						<td>${analysis.afterQEnd.missingFromFolder.size}</td>
						<td>${analysis.afterQEnd.missingFromDatabase.size}</td>
					</tr>
				</c:if>
			</tbody>
		</table>
		<c:if test="${analysis.problems.nosProblems > 0}">
		<hr>
		<h2>Problems</h2>
		<table class="table table-striped table-condensed">
			<thead>
				<tr>
					<th class="span4">Problem type</th>
					<th class="span2">Count</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>Metadata file missing</td>
					<td>${analysis.problems.metadataMissing}
				</tr>
				<tr>
					<td>Metadata file size wrong</td>
					<td>${analysis.problems.metadataSize}
				</tr>
				<tr>
					<td>Grabbed Datafile missing</td>
					<td>${analysis.problems.fileMissing}
				</tr>
				<tr>
					<td>Grabbed Datafile size doesn't match recorded size</td>
					<td>${analysis.problems.fileSize}
				</tr>
				<tr>
					<td>Grabbed Datafile size doesn't match current file size in S:</td>
					<td>${analysis.problems.fileSize2}
				</tr>
			</tbody>
		</table>
		<table class="table table-striped table-condensed">
			<thead>
				<tr>
					<th class="span1">Dataset Id</th>
					<th class="span1">Problem Type</th>
					<th class="span5">Details</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${analysis.problems.problems}" var="problem">
					<tr>
						<td><a href="/paul/datasets/${problem.dataset.id}">${problem.dataset.id}</a></td>
						<td>${problem.type}</td>
						<td>${problem.details}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		</c:if>
		<c:if test="${analysis.all.missingFromFolder.size + analysis.all.missingFromDatabase.size > 0}">
			<table class="table table-striped table-condensed">
				<thead>
					<tr>
					    <th>Timespan</th>
						<th colspan="2">Datasets missing from Queues</th>
						<th colspan="2">Datasets missing from S:</th>
					</tr>
					<tr>
						<th class="span1">&nbsp;</th>
						<th class="span1">First</th>
						<th class="span1">Last</th>
						<th class="span1">First</th>
						<th class="span1">Last</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>all</td>
						<c:choose>
							<c:when test="${empty analysis.all.missingFromDatabase}">
								<td>-</td>
								<td>-</td>
							</c:when>
							<c:otherwise>
								<td>${analysis.all.missingFromDatabase.first.sourceFilePathnameBase}</td>
								<td>${analysis.all.missingFromDatabase.last.sourceFilePathnameBase}</td>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${empty analysis.all.missingFromFolder}">
								<td>-</td>
								<td>-</td>
							</c:when>
							<c:otherwise>
								<td>${analysis.all.missingFromFolder.first.id}</td>
								<td>${analysis.all.missingFromFolder.last.id}</td>
							</c:otherwise>
						</c:choose>
					</tr>
					<c:if test="${! empty analysis.beforeHWM}">
						<tr>
							<td>Before HWM</td>
							<c:choose>
								<c:when test="${empty analysis.beforeHWM.missingFromDatabase}">
									<td>-</td>
									<td>-</td>
								</c:when>
								<c:otherwise>
									<td>${analysis.beforeHWM.missingFromDatabase.first.sourceFilePathnameBase}</td>
									<td>${analysis.beforeHWM.missingFromDatabase.last.sourceFilePathnameBase}</td>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${empty analysis.beforeHWM.missingFromFolder}">
									<td>-</td>
									<td>-</td>
								</c:when>
								<c:otherwise>
									<td>${analysis.beforeHWM.missingFromFolder.first.id}</td>
									<td>${analysis.beforeHWM.missingFromFolder.last.id}</td>
								</c:otherwise>
							</c:choose>
						</tr>
					</c:if>
					<c:if test="${! empty analysis.afterHWM}">
						<tr>
							<td>After HWM</td>
							<c:choose>
								<c:when test="${empty analysis.afterHWM.missingFromDatabase}">
									<td>-</td>
									<td>-</td>
								</c:when>
								<c:otherwise>
									<td>${analysis.afterHWM.missingFromDatabase.first.sourceFilePathnameBase}</td>
									<td>${analysis.afterHWM.missingFromDatabase.last.sourceFilePathnameBase}</td>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${empty analysis.afterHWM.missingFromFolder}">
									<td>-</td>
									<td>-</td>
								</c:when>
								<c:otherwise>
									<td>${analysis.afterHWM.missingFromFolder.first.id}</td>
									<td>${analysis.afterHWM.missingFromFolder.last.id}</td>
								</c:otherwise>
							</c:choose>
						</tr>
					</c:if>
					<c:if test="${! empty analysis.beforeQEnd}">
						<tr>
							<td>Before current Queue End</td>
							<c:choose>
								<c:when test="${empty analysis.beforeQEnd.missingFromDatabase}">
									<td>-</td>
									<td>-</td>
								</c:when>
								<c:otherwise>
									<td>${analysis.beforeQEnd.missingFromDatabase.first.sourceFilePathnameBase}</td>
									<td>${analysis.beforeQEnd.missingFromDatabase.last.sourceFilePathnameBase}</td>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${empty analysis.beforeQEnd.missingFromFolder}">
									<td>-</td>
									<td>-</td>
								</c:when>
								<c:otherwise>
									<td>${analysis.beforeQEnd.missingFromFolder.first.id}</td>
									<td>${analysis.beforeQEnd.missingFromFolder.last.id}</td>
								</c:otherwise>
							</c:choose>
						</tr>
					</c:if>
					<c:if test="${! empty analysis.afterQEnd}">
						<tr>
							<td>After current Queue End</td>
							<c:choose>
								<c:when test="${empty analysis.afterQEnd.missingFromDatabase}">
									<td>-</td>
									<td>-</td>
								</c:when>
								<c:otherwise>
									<td>${analysis.afterQEnd.missingFromDatabase.first.sourceFilePathnameBase}</td>
									<td>${analysis.afterQEnd.missingFromDatabase.last.sourceFilePathnameBase}</td>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${empty analysis.afterQEnd.missingFromFolder}">
									<td>-</td>
									<td>-</td>
								</c:when>
								<c:otherwise>
									<td>${analysis.afterQEnd.missingFromFolder.first.id}</td>
									<td>${analysis.afterQEnd.missingFromFolder.last.id}</td>
								</c:otherwise>
							</c:choose>
						</tr>
					</c:if>
				</tbody>
			</table>
		</c:if>
	    <hr>
		<h2>Diagnosis</h2>
		<ul>
			<c:choose>
			<c:when test="${status.status == 'ON'}">
				<li>
					The Data Grabber is currently running for this Facility.
				</li>
			</c:when>
			<c:when test="${status.status == 'OFF'}">
				<li>
					The Data Grabber is currently not running for this Facility.
				</li>
			</c:when>
			<c:when test="${status.status == 'DISABLED'}">
				<li>
					This Facility is currently disabled.
				</li>
			</c:when>
			</c:choose>
			<c:choose>
				<c:when test="${empty status.grabberHWMTimestamp}">
				<li>
					It appears that the Data Grabber has never been activated for this facility.
				</li>
				</c:when>
				<c:when test="${empty catchupTimestamp}">
				<li>
					Any previously grabbed Datasets have either expired or been deleted.
					</li>
				</c:when>
				<c:when test="${catchupTimestamp.time == status.grabberHWMTimestamp.time}">
				<li>
					The last queued Dataset and HWM are in agreement.
					</li>
				</c:when>
				<c:when test="${catchupTimestamp.time < status.grabberHWMTimestamp.time}">
				<li>
					The last queued Dataset is before the HWM.  Maybe some recently
					grabbed Datasets were deleted from the queues.
					</li>
				</c:when>
				<c:when test="${catchupTimestamp.time > status.grabberHWMTimestamp.time}">
				<li>
					The last queued Dataset is after the HWM.
					</li>
				</c:when>
			</c:choose>
			<c:choose>
				<c:when test="${analysis.all.totalInFolder == analysis.all.totalMatching}">
					<li>
						All Datasets in the S: folder are queued.
					</li>
				</c:when>
				<c:otherwise>
					<li>
						There are ${analysis.all.totalInFolder - analysis.all.totalMatching} 
						Datasets in the S: folder that are not in the queues.
					</li>
				</c:otherwise>
			</c:choose>
			</ul>
		<h2>Actions</h2>
		<form method="post">
		    <c:set var="isohwm"><fmt:formatDate pattern="yyyy-MM-dd'T'HH:mm:ss" value="${hwmTimestamp}"/></c:set>
		    New HWM: <input type="text" name="hwmTimestamp" 
		        value="${isohwm}">
			<button type="submit" name="hwm">Recalculate Statistics</button>
			<button type="submit" name="setHWM">Set the HWM</button>
		</form>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>