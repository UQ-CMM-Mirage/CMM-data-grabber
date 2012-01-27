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
              <li>
                ${facility.facilityName} is ${facility.inUse ? 'in use' : 'idle'}
                <c:if test="${facility.inUse}">
                    - user : ${facility.currentSession.userName},
                      account : ${facility.currentSession.account},
                      started : ${facility.currentSession.loginTime}
                </c:if>
              </li>
            </c:if>
          </c:forEach>
        </ul>
    </body>
</html>