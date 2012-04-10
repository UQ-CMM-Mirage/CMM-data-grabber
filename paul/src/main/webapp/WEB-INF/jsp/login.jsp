<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber Access Control</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container">
		<h1>Data Grabber Login</h1>
		<c:choose>
			<c:when test="">
				<p>Login to the Data Grabber to claim files, view the queues and
					perform other tasks. Use your normal ACLS user name and password,
					or administrator credentials.</p>
			</c:when>
			<c:otherwise>
				<p>You need to be logged in to perform this operation</p>
			</c:otherwise>
		</c:choose>
		${message}

		<form name="form" method="POST" method="post"
			action='<%=response.encodeURL("j_security_check")%>'>
			<c:if test="${empty accounts}">
              User name: <input type="text" name="j_username"
					value="${param.userName}">
				<br>
              Password: <input type="password" name="j_password"
					value="${param.userName}">
			</c:if>
			<br>
			<button type="submit" name="startSession">Login</button>
			<button type="button" onclick="window.location = '/paul'">Cancel</button>
		</form>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>