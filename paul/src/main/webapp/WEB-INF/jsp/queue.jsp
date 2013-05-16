<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber Ingestion Queue Admin</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>Data Grabber Ingestion Queue Admin</h1>
		<c:choose>
			<c:when test="${!empty queue}">
				<form method="POST" action="ingestible">
					<button type="submit" name="deleteAll">Delete/Archive all
						entries</button>
					<button type="submit" name="expire">Expire entries</button>
				</form>
				<table class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
							<th>Entry #</th>
							<th>Facility name</th>
							<th>User</th>
							<th>Dataset capture date/time</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${queue}" var="entry">
							<tr>
								<td><a href="${entry.id}">${entry.id}</a></td>
								<td>${entry.facilityName}</td>
								<td>${entry.userName}</td>
								<td><fmt:formatDate value="${entry.captureTimestamp}" 
										type="both" dateStyle="medium"/></td>
								<td>
									<form method="POST" action="${entry.id}"
									      class="btn-group">
										<button class="btn" type="submit" name="mode" value="discard">Delete</button>
										<button class="btn" type="submit" name="mode" value="archive">Archive</button>
										<input type="hidden" name="confirmed"> <input
											type="hidden" name="delete">
									</form>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:when>
			<c:otherwise>
            Ingestion queue is empty
        </c:otherwise>
		</c:choose>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>