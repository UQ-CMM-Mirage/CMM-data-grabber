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
		<div class="page-header">
			<h1>
				<c:choose>
					<c:when test="${next == 'claimDatasets'}">
					Select the Instrument you were using
				</c:when>
					<c:when test="${next == 'facilityLogout'}">
					Select the Instrument you are using.
				</c:when>
					<c:when test="${next == 'facilityLogin'}">
					Select the Instrument you need to use
				</c:when>
					<c:otherwise>
					Facility Selection
				</c:otherwise>
				</c:choose>
				<c:if test="${! empty message}">
					<small>${message}</small>
				</c:if>
			</h1>

			<form class="form-horizontal" action="facilitySelect" method="post">
				<fieldset>
					<div class="controlGroup">
						<label class="control-label" for="selector">Instrument</label>
						<div class="controls">
							<select name="facilityName" id="selector" class="span4">
								<c:forEach items="${facilities}" var="facility">
									<option value="${facility.facilityName}">
										${facility.facilityName}</option>
								</c:forEach>
							</select>
						</div>
					</div>
					<input type="hidden" name="next" value="${next}">
					<div class="form-actions">
						<button class="btn btn-medium btn-primary" type="submit">OK</button>
						<button class="btn btn-medium" type="button"
							onclick="window.location = '/paul'">Cancel</button>
					</div>
				</fieldset>
			</form>
		</div>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>