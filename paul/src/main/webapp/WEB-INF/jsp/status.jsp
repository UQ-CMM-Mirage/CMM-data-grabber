<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Facility Status</title>
    </head>
    <body>
        <h1>ACLS Facility Status</h1>
        <ul>
          <c:forEach items="${sessions}" var="session">
            <li>${session.facility.facilityId} is ${session.inUse ? 'in use' : 'idle'}</li>
          </c:forEach>
        </ul>
    </body>
</html>