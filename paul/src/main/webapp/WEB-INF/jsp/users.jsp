<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>ACLS Proxy / Data Grabber User Management</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>ACLS Proxy / Data Grabber User Management</h1>
		The following ACLS accounts are known to the proxy.  Either the user
		has successfully logged into ACLS via the proxy at some point in the
		past, or the account has been added administratively via this page.  
		(The latter does not allow the user to login, but it allows CMM staff
		to tell the Data Grabber that files may be assigned to the user for 
		transfer to Mirage.)
		<h2>Known Users</h2>
		<ul>
			<c:forEach items="${userNames}" var="userName">
				<li><a href="users/${userName}">${userName}</a></li>
			</c:forEach>
		</ul>
		<br>
		<b>${message}</b>
		<br>
		<form action="users" method="post">
			User name:<input name="userName" type="text">
			<button type="submit" name="add" value="add">Add User</button>
			<button type="submit" name="remove" value="remove">Remove User</button>
		</form>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>