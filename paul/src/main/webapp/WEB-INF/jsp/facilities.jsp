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
					<th class="span3">Facility</th>
					<th class="span3">Description</th>
					<th class="span2">Data Grabbing</th>
					<th class="span4"></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${facilities}" var="facility">
					<tr>
						<td><a href="facilities/${facility.facilityName}">
								${facility.facilityName}</a></td>
						<td>${facility.facilityDescription}</td>
						<td>
							${facility.status.status}
							<c:if test="${! empty facility.status.message}">
								<br>${facility.status.message}
							</c:if>
						</td>
						<td class="form-inline,btn-toolbar">
							<form action="facilities/${facility.facilityName}" method="post">
								<c:choose>
									<c:when test="${facility.status.status == 'ON'}">
										<button class="btn" type="submit" name="stop">Stop</button>
									</c:when>
									<c:when test="${facility.status.status == 'OFF'}">
										<button class="btn" type="submit" name="start">Start</button>
									</c:when>
									<c:otherwise>
										<button class="btn" type="submit" name="start"
												disabled="disabled">Start</button>
									</c:otherwise>
								</c:choose>
								<button class="btn" type="submit" name="sessionLog">Session Log</button>
								<button class="btn" type="submit" name="copy">Copy</button>
								<button class="btn" type="submit" name="delete">Delete</button>
							</form>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="form-inline">
			<form action="/paul/facilities" method="get">
				<button class="btn" type="submit" name="newForm">Add a new facility</button>
			</form>
		</div>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>