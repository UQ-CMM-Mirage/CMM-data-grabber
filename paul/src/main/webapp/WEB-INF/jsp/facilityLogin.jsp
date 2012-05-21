<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber - Instrument Login</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>ACLS Instrument Login</h1>

		<c:choose>
			<c:when test="${! empty message}">
				<div class="alert alert-error">${message}</div>
			</c:when>
			<c:otherwise>
				<div class="alert alert-information">Please enter your normal
					ACLS username and password. (You will be prompted for your ACLS
					account name if you have multiple ACLS accounts.)</div>
			</c:otherwise>
		</c:choose>

		<form class="form-horizontal" name="form" method="post"
			action="/paul/facilityLogin">
			<fieldset>
				<div class="control-group">
					<label class="control-label" for="userName">User name</label> 
					<input type="text" name="userName" id="userName"
					    value="${userName}" ${! empty accounts ? 'readonly' : ''}>
				</div>
				<div class="control-group">
					<label class="control-label" for="password">Password</label> 
					<input type="password" name="password" id="password" 
						value="${password}" ${! empty accounts ? 'readonly' : ''}>
				</div>
				<c:if test="$! empty accounts}">
					<div class="control-group">
						<label class="control-label" for="account">Account</label> <select
							name="account" id="account">
							<c:forEach items="${accounts}" var="account">
								<option value="${account}">${account}</option>
							</c:forEach>
						</select>
					</div>
				</c:if>
				<input type="hidden" name="facilityName" value="${facilityName}">
				<input type="hidden" name="returnTo" value="${returnTo}">
				<div class="form-actions">
				<c:choose>
					<c:when test='${empty inUse}'>
						<button class="btn btn-primary" type="submit" name="startSession">Start session</button>
					</c:when>
					<c:otherwise>
						<button class="btn btn-danger" type="submit" name="startSession">Override existing
							session</button>
						<input type="hidden" name="endOldSession" value="yes">
					</c:otherwise>
				</c:choose>
				<button type="button" onclick="window.location = '${returnTo}'">Cancel</button>
				</div>
			</fieldset>
		</form>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>