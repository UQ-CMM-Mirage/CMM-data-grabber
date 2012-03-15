<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Data Grabber Operation Succeeded</title>
    </head>
    <body>
        <h1>Data Grabber Operation Succeeded</h1>
        ${message}
        <br>
        <button onclick="window.location = '${returnTo}'">OK</button>
    </body>
</html>