<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber Facility Select</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>Facilities</h1>
		<table class="table table-striped table-condensed">
			<thead>
				<tr>
					<th>Current Facilities</th>
				</tr>
				<tr>
					<th class="span4">Facility</th>
					<th class="span4">Description</th>
					<th class="span4">Status</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${facilities}" var="facility">
					<tr>
						<td><a href="/paul/facilities/${facility.facilityName}">
								${facility.facilityName}</a></td>
						<td>${facility.facilityDescription}</td>
						<td>${facility.status}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="form-inline">
			<form action="/paul/facilities" method="get">
				<button type="submit" name="newForm">Add a new facility</button>
			</form>
			<form>
				<button type="submit">Copy a facility</button>
				<input type="text" name="facilityName">
			</form>
		</div>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>