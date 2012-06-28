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
				<tr><th class="span2">Time range</th>
					<th class="span2">All Datasets on S:</th>
					<th class="span2">Duplicate Datasets on S:</th>
					<th class="span2">All Datasets in queues</th>
					<th class="span2">Duplicate Datasets in queues</th>
					<th class="span2">Datasets on S: with Datasets in queues</th>
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
				</tr>
				<c:if test="${!empty analysis.beforeHWM}">
					<tr>
						<td>before HWM</td>
						<td>${analysis.beforeHWM.totalInFolder}</td>
						<td>${analysis.beforeHWM.multipleInFolder}</td>
						<td>${analysis.beforeHWM.totalInDatabase}</td>
						<td>${analysis.beforeHWM.multipleInDatabase}</td>
						<td>${analysis.beforeHWM.totalMatching}</td>
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
					</tr>
				</c:if>
			</tbody>
		</table>
		<hr>
		<h2>Diagnosis</h2>
		<p>
			<c:choose>
				<c:when test="${status.status == 'ON'}">
					The Data Grabber is currently running for this Facility.  No catchup action required.
				</c:when>
				<c:when test="${status.status == 'DISABLED'}">
					This Facility is disabled.
				</c:when>
				<c:when test="${empty status.grabberHWMTimestamp}">
					It appears that the Data Grabber has never been activated for this facility.
				</c:when>
				<c:when test="${empty catchupTimestamp}">
					It appears that all previously grabbed Datasets have either expired or been deleted.
				</c:when>
				<c:when test="${catchupTimestamp.time == status.grabberHWMTimestamp.time}">
					The catchup time and HWM are in agreement.  All is well.
				</c:when>
				<c:when test="${catchupTimestamp.time < status.grabberHWMTimestamp.time}">
					The catchup time is before the HWM.  It appears that some of the most recently
					grabbed Datasets have been manually deleted.
				</c:when>
				<c:when test="${catchupTimestamp.time > status.grabberHWMTimestamp.time}">
					The catchup time is after the HWM.  It is unclear how this could have happened.
				</c:when>
			</c:choose>
		</p>
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