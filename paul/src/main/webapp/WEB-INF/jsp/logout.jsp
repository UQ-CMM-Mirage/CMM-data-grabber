<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="true"%>

<html>
    <head>
        <title>Data Grabber Access Control</title>
    </head>
    <body>
        <h1>Data Grabber Access Control</h1>
        You have been logged out.
		<% session.invalidate(); %>
    </body>
</html>