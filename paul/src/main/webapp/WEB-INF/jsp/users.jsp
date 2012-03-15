<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>Data Grabber Users</title>
</head>
<body>
	<h1>Data Grabber Known Users</h1>
	<ul>
		<c:forEach items="${userNames}" var="userName">
			<li><a href="users/${userName}">${userName}</a></li>
		</c:forEach>
	</ul>
</body>
</html>