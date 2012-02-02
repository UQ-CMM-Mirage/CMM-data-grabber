<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Paul Ingestion Queue Admin</title>
    </head>
    <body>
        <h1>Paul Ingestion Queue Admin</h1>
        <ul>
             <c:forEach items="${queue}" var="entry">
                <li><a href="queue/${entry.id}">Entry # ${entry.id}</a> - 
                    facility ${entry.facilityName}, 
                    user : ${entry.userName}, 
                    captured: ${entry.captureTimestamp}
                </li>
             </c:forEach>
        </ul>
    </body>
</html>