<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Facility Status</title>
    </head>
    <body>
        <h1>ACLS Facility Status</h1>
        <ul>
          <c:forEach items="${facilities}" var="facility">
            <c:if test="${!facility.dummy}">
              <li>${facility.facilityId} is ${facility.inUse ? 'in use' : 'idle'}</li>
            </c:if>
          </c:forEach>
        </ul>
    </body>
</html>