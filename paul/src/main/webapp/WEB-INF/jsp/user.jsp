<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>Data Grabber User</title>
</head>
<body>
	<h1>Data Grabber User ${user.userName}</h1>
	<ul>
		<li>User name: ${user.userName}</li>
		<li>Email address: ${user.emailAddress}</li>
		<li>Real name: ${user.humanReadableName}</li>
	</ul>
</body>
</html>