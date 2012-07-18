<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/paulTags" prefix="pt"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber Catchup Control</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<div class="row-fluid"><h1>Catchup Control for ${facilityName}</h1></div>
		<div class="row-fluid">
			Grabber status : ${status.status} <br> 
			Grabber message : ${status.message} <br> 
			Grabber LWM timestamp : 
				<fmt:formatDate pattern="yyyy-MM-dd'T'HH:mm:ss"
						value="${status.grabberLWMTimestamp}"/> <br>
			Grabber HWM timestamp : 
				<fmt:formatDate pattern="yyyy-MM-dd'T'HH:mm:ss"
						value="${status.grabberHWMTimestamp}"/> <br>
			Timestamp of last queued Dataset : 
				<fmt:formatDate pattern="yyyy-MM-dd'T'HH:mm:ss"
						value="${catchupTimestamp}"/> <br>
			LWM / HWM used in analysis : 
				<fmt:formatDate pattern="yyyy-MM-dd'T'HH:mm:ss"
						value="${lwmTimestamp}"/> / 
				<fmt:formatDate pattern="yyyy-MM-dd'T'HH:mm:ss"
						value="${hwmTimestamp}"/>
		</div>
		<div class="row-fluid"><h2>Statistics</h2></div>
		<div class="row-fluid">
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
				<c:if test="${!empty analysis.beforeLWM}">
					<tr>
						<td>&#x2264; LWM</td>
						<td>${analysis.beforeLWM.totalInFolder}</td>
						<td>${analysis.beforeLWM.multipleInFolder}</td>
						<td>${analysis.beforeLWM.totalInDatabase}</td>
						<td>${analysis.beforeLWM.multipleInDatabase}</td>
						<td>${analysis.beforeLWM.totalMatching}</td>
						<td>${analysis.beforeLWM.missingFromFolder.size}</td>
						<td>${analysis.beforeLWM.missingFromDatabase.size}</td>
					</tr>
				</c:if>
				<c:if test="${!empty analysis.afterLWM}">
					<tr>
						<td>&gt; LWM</td>
						<td>${analysis.afterLWM.totalInFolder}</td>
						<td>${analysis.afterLWM.multipleInFolder}</td>
						<td>${analysis.afterLWM.totalInDatabase}</td>
						<td>${analysis.afterLWM.multipleInDatabase}</td>
						<td>${analysis.afterLWM.totalMatching}</td>
						<td>${analysis.afterLWM.missingFromFolder.size}</td>
						<td>${analysis.afterLWM.missingFromDatabase.size}</td>
					</tr>
				</c:if>
				<c:if test="${!empty analysis.beforeHWM}">
					<tr>
						<td>&#x2264; HWM</td>
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
						<td>&gt; HWM</td>
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
						<td>&#x2264; current Queue End</td>
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
						<td>&gt; current Queue End</td>
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
		</div>
		<c:if test="${analysis.problems.nosProblems > 0}">
				<div class="row-fluid"><h2>Problems</h2></div>
				<div class="row-fluid">
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
								<td>Grabbed Datafile size doesn't match current file size
									in S:</td>
								<td>${analysis.problems.fileSize2}
							</tr>
						</tbody>
					</table>
				</div>
				<div class="row-fluid">
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
				</div>
		</c:if>
		<c:if test="${analysis.all.missingFromFolder.size + analysis.all.missingFromDatabase.size > 0}">
			<div class="row-fluid"><h2>Missing Datasets (between LWM and HWM)</h2></div>
			<div class="row-fluid">
				<div class="span6">
					<table class="table table-striped table-condensed">
						<thead>
							<tr>
								<th colspan="2">Datasets missing from Queues</th>
							</tr>
							<tr>
								<th class="span1">Timespans</th>
								<th class="span5">Details</th>
							</tr>
						</thead>
						<tbody>
							<pt:missingDatasets fromQueue="true">
								<tr>
									<td>${timespans}</td>
									<td>${missing.sourceFilePathnameBase} | <fmt:formatDate
											pattern="yyyy-MM-dd'T'HH:mm:ss"
											value="${missing.lastFileTimestamp}" />
									</td>
								</tr>
							</pt:missingDatasets>
						</tbody>
					</table>
				</div>
				<div class="span6">
					<table class="table table-striped table-condensed">
						<thead>
							<tr>
								<th colspan="2">Datasets missing from S:</th>
							</tr>
							<tr>
								<th class="span1">Timespans</th>
								<th class="span5">Missing Dataset details</th>
							</tr>
						</thead>
						<tbody>
							<pt:missingDatasets fromQueue="false">
								<tr>
									<td>${timespans}</td>
									<td><a href="/paul/datasets/${missing.id}">Dataset #${missing.id}</a>
										- ${missing.sourceFilePathnameBase} | <fmt:formatDate
											pattern="yyyy-MM-dd'T'HH:mm:ss"
											value="${missing.lastFileTimestamp}" /></td>
								</tr>
							</pt:missingDatasets>
						</tbody>
					</table>
				</div>
			</div>
		</c:if>
		<div class="row-fluid">
			<h2>Actions</h2>
		</div>

		<form method="post">
			<div class="row-fluid">
				<c:set var="isohwm">
					<fmt:formatDate pattern="yyyy-MM-dd'T'HH:mm:ss"
						value="${hwmTimestamp}" />
				</c:set>
				New HWM: <input type="text" name="hwmTimestamp" value="${isohwm}">
				<button type="submit" name="setHWM">Change the HWM</button>
			</div>
			<div class="row-fluid">
				<c:set var="isolwm">
					<fmt:formatDate pattern="yyyy-MM-dd'T'HH:mm:ss"
						value="${lwmTimestamp}" />
				</c:set>
				New LWM: <input type="text" name="lwmTimestamp" value="${isolwm}">
				<button type="submit" name="setLWM">Change the LWM</button>
			</div>
			<div class="row-fluid">
				<button type="submit" name="analyse">Reanalyse with
					proposed LWM / HWM</button>
			</div>
		</form>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>