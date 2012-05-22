<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="true"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber Access Control</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>Data Grabber Login</h1>
		<c:choose>
			<c:when test="${empty message}">
				<div class="alert alert-information">Please enter your normal
					ACLS username and password, or the credentials of a local
					administrator account.</div>
			</c:when>
			<c:otherwise>
				<div class="alert alert-error">${message}</div>
			</c:otherwise>
		</c:choose>
		<form class="form-horizontal" name="form" method="post"
			action='<%=response.encodeURL("j_security_check")%>'>
			<fieldset>
				<c:if test="${empty accounts}">
					<div class="control-group">
						<label class="control-label" for="userName">User name</label> 
						<div class="controls">
							<input type="text" name="j_username" id="userName"
								value="${param.userName}">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="password">Password</label> 
						<div class="controls"> 
							<input type="password" name="j_password" id="password"
								value="${param.userName}">
					    </div>
					</div>
				</c:if>
				<div class="form-actions">
					<button class="btn btn-small btn-primary" type="submit" 
						name="startSession">Login</button>
					<button class="btn btn-small" type="button" 
						onclick="window.location = '/paul'">Cancel</button>
				</div>
			</fieldset>
		</form>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>