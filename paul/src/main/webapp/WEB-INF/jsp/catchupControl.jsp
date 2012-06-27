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
		Grabber status : ${status.status}
		<br>
		Grabber message : ${status.message}
		<br>
		Grabber HWM timestamp : ${status.grabberHWMTimestamp}
		<br>
		Timestamp of last queued Dataset : ${catchupTimestamp}
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
			<c:when test="${catchupTimestamp.time == status.grabberHWMTimestamp}">
				The catchup time and HWM are in agreement.  All is well.
			</c:when>
			<c:when test="${catchupTimestamp.time < status.grabberHWMTimestamp}">
				The catchup time is before the HWM.  It appears that some of the most recently
				grabbed Datasets have been manually deleted.
			</c:when>
			<c:when test="${catchupTimestamp.time > status.grabberHWMTimestamp}">
				The catchup time is after the HWM.  It is unclear how this could have happened.
			</c:when>
		</c:choose>
		</p>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>